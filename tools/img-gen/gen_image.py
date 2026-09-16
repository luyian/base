#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
通用图片生成工具（商汤大模型绘图）。

不限于图标：任意 prompt 都能生成图片并保存到本地。

配置来源：
  直接连接 ai_video_studio 项目的 MySQL，读取商汤渠道（ai_channel.channel_code=sensenova）
  的 base_url、api_key，以及该渠道下 model_type=IMAGE 的模型（ai_model.model_code）。
  也支持 --api-key 覆盖（不读库）。

调用方式（同 ai-video-studio 的 SenseNovaImageAdapter）：
  POST {base_url}/images/generations   body = {model, prompt, size, n:1}
  鉴权  Authorization: Bearer <api_key>
  响应图片字段逐层兼容：data/output/images/root → url 或 b64_json。

用法：
  # 生成一张任意图片（保存原图）
  python gen_image.py --prompt "一只简约风格的橘猫，扁平插画" --out cat.png

  # 生成首页图标风格（白描线性 + 透明背景 + 缩放）
  python gen_image.py --prompt "..." --out icon.png --icon --scale 300

  # 指定尺寸（须在商汤白名单内，默认 2048x2048）
  python gen_image.py --prompt "..." --out wide.png --size 2752x1536

  # 不用数据库，直接指定 key
  python gen_image.py --prompt "..." --out a.png --api-key sk-xxx

尺寸白名单：1664x2496 2496x1664 1760x2368 2368x1760 1824x2272 2272x1824
            2048x2048 2752x1536 1536x2752 3072x1376 1344x3136 2560x720 3072x864
"""

import argparse
import base64
import io
import json
import os
import sys
import time

import requests
import pymysql

CHANNEL_CODE = "sensenova"
MODEL_TYPE_IMAGE = "IMAGE"
DEFAULT_SIZE = "2048x2048"
DEFAULT_BASE_URL = "https://token.sensenova.cn/v1"
TIMEOUT = 180

ALLOWED_SIZES = {
    "1664x2496", "2496x1664", "1760x2368", "2368x1760", "1824x2272",
    "2272x1824", "2048x2048", "2752x1536", "1536x2752", "3072x1376",
    "1344x3136", "2560x720", "3072x864",
}


def load_config(db_host, db_port, db_user, db_password, db_name):
    """从 ai_video_studio 库读取商汤渠道与图像模型配置。"""
    conn = pymysql.connect(
        host=db_host, port=db_port, user=db_user, password=db_password,
        database=db_name, charset="utf8mb4", cursorclass=pymysql.cursors.DictCursor,
    )
    try:
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id, channel_name, base_url, api_key FROM ai_channel "
                "WHERE deleted = 0 AND status = 1 AND channel_code = %s "
                "ORDER BY priority ASC, id ASC LIMIT 1",
                (CHANNEL_CODE,),
            )
            channel = cur.fetchone()
            if not channel:
                raise SystemExit(f"未找到可用商汤渠道（ai_channel.channel_code={CHANNEL_CODE}）")
            cur.execute(
                "SELECT model_name, model_code FROM ai_model "
                "WHERE deleted = 0 AND status = 1 AND channel_id = %s AND model_type = %s "
                "ORDER BY sort ASC, id ASC LIMIT 1",
                (channel["id"], MODEL_TYPE_IMAGE),
            )
            model = cur.fetchone()
            if not model:
                raise SystemExit(f"渠道 {channel['channel_name']} 下未找到图片模型（model_type=IMAGE）")
            return {
                "api_key": channel["api_key"],
                "base_url": channel["base_url"] or "",
                "model_name": model["model_name"],
                "model_code": model["model_code"],
            }
    finally:
        conn.close()


def normalize_base_url(base_url):
    """把渠道 base_url 归一化成图片生成接口地址。"""
    url = (base_url or "").strip()
    if not url:
        return DEFAULT_BASE_URL + "/images/generations"
    url = url.rstrip("/")
    if url.endswith("/images/generations"):
        return url
    if url.endswith("/chat/completions"):
        return url[: -len("/chat/completions")] + "/images/generations"
    return url + "/images/generations"


def generate(config, prompt, size, dry_run):
    """调用商汤图片生成接口，返回图片原始字节。"""
    images_url = normalize_base_url(config["base_url"])
    payload = {"model": config["model_code"], "prompt": prompt, "size": size, "n": 1}
    headers = {
        "Authorization": "Bearer " + config["api_key"],
        "Content-Type": "application/json",
        "Accept": "application/json",
    }
    if dry_run:
        print("[dry-run] url   :", images_url)
        print("[dry-run] model :", payload["model"])
        print("[dry-run] size  :", payload["size"])
        print("[dry-run] prompt:", prompt)
        return None

    resp = requests.post(images_url, headers=headers, data=json.dumps(payload), timeout=TIMEOUT)
    if resp.status_code != 200:
        raise RuntimeError(f"商汤接口返回 {resp.status_code}：{resp.text[:500]}")
    body = resp.json()

    image = _extract_image(body)
    if image is None:
        raise RuntimeError(f"响应中未解析到图片：{json.dumps(body, ensure_ascii=False)[:500]}")
    if isinstance(image, dict):
        image = _pick_url_or_b64(image)
    elif isinstance(image, str) and image.startswith("{"):
        image = _pick_url_or_b64(json.loads(image))
    if not image or not isinstance(image, str):
        raise RuntimeError("响应中未找到图片 url 或 base64 字段")

    if image.startswith("data:"):
        image = image.split(",", 1)[1]
    if image.startswith("http://") or image.startswith("https://"):
        pic = requests.get(image, timeout=TIMEOUT)
        if pic.status_code != 200:
            raise RuntimeError(f"下载图片失败 {pic.status_code}")
        return pic.content
    return base64.b64decode(image)


def _extract_image(obj):
    if isinstance(obj, list):
        for item in obj:
            found = _extract_image(item)
            if found is not None:
                return found
        return None
    if not isinstance(obj, dict):
        return None
    for key in ("data", "output", "images", "root", "result"):
        if key in obj:
            found = _extract_image(obj[key])
            if found is not None:
                return found
    if _pick_url_or_b64(obj) is not None:
        return obj
    for v in obj.values():
        found = _extract_image(v)
        if found is not None:
            return found
    return None


def _pick_url_or_b64(obj):
    if not isinstance(obj, dict):
        return None
    for key in ("image_url", "url", "output_url", "origin_image_url", "imageUrl", "outputUrl"):
        if obj.get(key):
            return obj[key]
    for key in ("b64_json", "base64", "image_base64", "imageBase64"):
        if obj.get(key):
            return obj[key]
    return None


def _is_watermark_pixel(r, g, b):
    """商汤水印特征：低饱和且亮度落在 150~238 灰阶。"""
    mx, mn = max(r, g, b), min(r, g, b)
    lum = (r + g + b) / 3.0
    return (mx - mn) < 25 and 150 < lum < 238


def _fill_watermark_region(im):
    """彩色图标去水印：右下角矩形内的中灰水印像素，用区域内平均背景色填充（保持不透明）。"""
    w, h = im.size
    x0, y0 = int(w * 0.80), int(h * 0.88)
    px = im.load()
    # 先用非水印像素估算区域背景色
    rs = gs = bs = n = 0
    for y in range(y0, h, 2):
        for x in range(x0, w, 2):
            r, g, b = px[x, y]
            if not _is_watermark_pixel(r, g, b):
                rs += r; gs += g; bs += b; n += 1
    if n == 0:
        return im
    bg = (rs // n, gs // n, bs // n)
    for y in range(y0, h):
        for x in range(x0, w):
            r, g, b = px[x, y]
            if _is_watermark_pixel(r, g, b):
                px[x, y] = bg
    return im


def process_icon(raw, out_scale, style="white"):
    """图标处理。style=white：白描线性 + 透明背景；style=color：保留彩色 + 背景色填充去水印。"""
    try:
        from PIL import Image, ImageFilter, ImageOps
    except ImportError:
        raise SystemExit("需要 Pillow：pip install Pillow")

    im = Image.open(io.BytesIO(raw)).convert("RGB")
    w, h = im.size
    side = min(w, h)
    im = im.crop(((w - side) // 2, (h - side) // 2, (w + side) // 2, (h + side) // 2))

    if style == "color":
        # 彩色图标：去水印（背景色填充）→ 缩放，保留原色彩
        im = _fill_watermark_region(im)
        return im.resize((out_scale, out_scale), Image.LANCZOS)

    # 白描图标：白图形 + 透明背景
    gray = im.convert("L")
    pw, ph = gray.size
    corners = [gray.getpixel((pw - 3, ph - 3)), gray.getpixel((3, ph - 3)),
               gray.getpixel((pw - 3, 3)), gray.getpixel((3, 3))]
    background = sum(corners) / 4.0
    bright, dark = 0, 0
    for y in range(ph):
        for x in range(pw):
            v = gray.getpixel((x, y))
            if v > 215:
                bright += 1
            elif v < 40:
                dark += 1

    # 背景亮→图形暗（反相）；背景暗→图形亮（亮度即 alpha）
    alpha = gray.point(lambda v: 255 - v) if (background > 175 or dark > bright) else gray

    # 去水印：右下角矩形内中灰像素透明化，区域外不动、深线条保留
    wm_x0, wm_y0 = int(pw * 0.80), int(ph * 0.88)
    rgb_px = im.load()
    alpha_px = alpha.load()
    for y in range(wm_y0, ph):
        for x in range(wm_x0, pw):
            r, g, b = rgb_px[x, y]
            if _is_watermark_pixel(r, g, b):
                alpha_px[x, y] = 0

    alpha = ImageOps.autocontrast(alpha)
    result = Image.merge(
        "RGBA",
        (Image.new("L", alpha.size, 255), Image.new("L", alpha.size, 255),
         Image.new("L", alpha.size, 255), alpha.filter(ImageFilter.GaussianBlur(1))),
    )
    result = result.resize((out_scale, out_scale), Image.LANCZOS)
    # 末道清理：低 alpha 弱边缘彻底透明，避免在渐变底色上泛白
    r, g, b, a = result.split()
    a = a.point(lambda v: v if v > 35 else 0)
    return Image.merge("RGBA", (r, g, b, a))


def resolve_size(size):
    """校验尺寸在白名单内；非法则回退默认方形。"""
    normalized = (size or "").replace("*", "x").replace("×", "x").lower()
    return normalized if normalized in ALLOWED_SIZES else DEFAULT_SIZE


def main():
    parser = argparse.ArgumentParser(description="商汤大模型通用图片生成工具")
    parser.add_argument("--prompt", required=True, help="图片描述（支持中文）")
    parser.add_argument("--out", required=True, help="输出文件路径（.png/.jpg）")
    parser.add_argument("--size", default=DEFAULT_SIZE, help="生成尺寸（须在白名单内）")
    parser.add_argument("--icon", action="store_true", help="图标处理模式：裁剪正方形 + 缩放 + 去水印")
    parser.add_argument("--style", choices=["white", "color"], default="white",
                        help="图标风格：white=白描透明背景，color=保留彩色不透明")
    parser.add_argument("--scale", type=int, default=300, help="--icon 模式输出边长(px)")
    parser.add_argument("--dry-run", action="store_true", help="只打印请求，不调用")
    parser.add_argument("--retries", type=int, default=3, help="失败重试次数")
    parser.add_argument("--api-key", default=None, help="覆盖读库得到的 api_key")
    parser.add_argument("--db-host", default=os.environ.get("DB_HOST", "127.0.0.1"))
    parser.add_argument("--db-port", type=int, default=int(os.environ.get("DB_PORT", "3306")))
    parser.add_argument("--db-user", default=os.environ.get("DB_USERNAME", "root"))
    parser.add_argument("--db-password", default=os.environ.get("DB_PASSWORD", "000000"))
    parser.add_argument("--db-name", default=os.environ.get("DB_NAME", "ai_video_studio"))
    args = parser.parse_args()

    size = resolve_size(args.size)

    print("== 读取 ai_video_studio 商汤渠道配置 ==")
    config = load_config(args.db_host, args.db_port, args.db_user, args.db_password, args.db_name)
    if args.api_key:
        config["api_key"] = args.api_key
    mask = (config["api_key"][:5] + "****") if config["api_key"] and len(config["api_key"]) > 5 else "****"
    print(f"  图片模型 : {config['model_name']} ({config['model_code']})")
    print(f"  base_url : {normalize_base_url(config['base_url'])}")
    print(f"  api_key  : {mask}")
    print(f"  尺寸     : {size}")

    if args.dry_run:
        print("== 预演（不调用） ==")
        generate(config, args.prompt, size, dry_run=True)
        print("[dry-run] 完成。")
        return

    raw = None
    for attempt in range(1, args.retries + 1):
        try:
            raw = generate(config, args.prompt, size, dry_run=False)
            break
        except Exception as exc:  # noqa: BLE001
            print(f"  第 {attempt} 次尝试失败：{exc}")
            if attempt < args.retries:
                time.sleep(5)
    if raw is None:
        raise SystemExit("生成失败，已达最大重试次数。")

    out_dir = os.path.dirname(os.path.abspath(args.out))
    os.makedirs(out_dir, exist_ok=True)
    if args.icon:
        result = process_icon(raw, args.scale, style=args.style)
        result.save(args.out, "PNG")
        print(f"已保存（图标模式 {args.style} {result.size[0]}x{result.size[1]}）：{args.out}")
    else:
        with open(args.out, "wb") as f:
            f.write(raw)
        print(f"已保存原图（{len(raw)} 字节）：{args.out}")


if __name__ == "__main__":
    main()
