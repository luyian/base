#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
元灵工具箱首页 PDF 图标生成工具（商汤大模型绘图）。

思路：
  1. 直接连接 ai_video_studio 项目的 MySQL，读取商汤渠道（ai_channel）配置：
     渠道编码 sensenova → base_url、api_key
     并读取该渠道下 model_type=IMAGE 的模型（ai_model）→ 模型标识 model_code
  2. 对压缩/整理/转换三个主题分别调用商汤图片生成接口
     POST {base_url}/images/generations   body = {model, prompt, size, n:1}
     鉴权  Authorization: Bearer <api_key>
  3. 下载生成的 2048x2048 图，用 PIL 抠成「白色简约图形 + 透明背景」的 PNG，
     缩放至小尺寸，输出到 ling-tools/assets/，供首页渐变方块上叠加显示。

用法示例：
  python gen_icons.py                      # 用数据库配置生成三张图
  python gen_icons.py --api-key xxx        # 覆盖 api_key（不读库）
  python gen_icons.py --dry-run            # 只打印配置与构造的请求，不调用
"""

import argparse
import base64
import json
import os
import sys
import time

import requests
import pymysql

# ------------------------- 常量 -------------------------
# 商汤通道编码（ai_channel.channel_code）
CHANNEL_CODE = "sensenova"
# 图像功能类型（ai_model.model_type）
MODEL_TYPE_IMAGE = "IMAGE"

# 图片生成尺寸（商汤白名单，取方形）
SIZE = "2048x2048"
# 生成数量
N = 1
# 接口超时（图片生成较慢）
TIMEOUT = 180

# 输出的 scale（最终 icon 边长，rpx 图在 62rpx 展示，保留 2 倍密度余量）
OUTPUT_SCALE = 300

# 相对本项目根目录的小程序 assets 目录（base/tools/img-gen → base/ling-tools/assets）
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
ASSETS_DIR = os.path.normpath(os.path.join(BASE_DIR, "..", "..", "ling-tools", "assets"))

# 三个 PDF 工具图标定义（现代可爱风：Q版圆润造型 + 柔和渐变底色，各自色系呼应首页）
ICONS = [
    {
        "name": "compress",
        "filename": "icon-compress.png",
        # 珊瑚橙色系：Q版文件盒 + 向下压缩箭头
        "prompt": (
            "现代可爱风格手机应用图标，圆角方形构图，柔和珊瑚橙到暖粉色渐变底色。"
            "中央是一个Q版圆润的可爱文件档案盒卡通形象，盒子简洁呆萌，盒面有一个"
            "白色简约向下箭头表示压缩，带一点白色高光。扁平插画风，马卡龙色系，"
            "柔和光影，造型圆润无棱角，画面干净简洁，无文字。"
        ),
    },
    {
        "name": "arrange",
        "filename": "icon-arrange.png",
        # 奶黄金色系：三本Q版纸张整齐叠放
        "prompt": (
            "现代可爱风格手机应用图标，圆角方形构图，柔和奶黄到金黄色渐变底色。"
            "中央是三本Q版圆润的可爱纸张文档整齐叠放排列的卡通形象，纸张边缘圆润，"
            "微微错开表现整理排序，带一点白色高光。扁平插画风，马卡龙色系，"
            "柔和光影，造型圆润无棱角，画面干净简洁，无文字。"
        ),
    },
    {
        "name": "convert",
        "filename": "icon-convert.png",
        # 天蓝色系：Q版双向循环箭头
        "prompt": (
            "现代可爱风格手机应用图标，圆角方形构图，柔和天蓝到淡蓝色渐变底色。"
            "中央是两个Q版圆润的可爱双向循环箭头卡通形象，箭头一上一下表示文件"
            "格式互换转换，带一点白色高光。扁平插画风，马卡龙色系，柔和光影，"
            "造型圆润无棱角，画面干净简洁，无文字。"
        ),
    },
]


def load_channel_config(prompt_no, db_host, db_port, db_user, db_password, db_name):
    """从 ai_video_studio 库读取商汤渠道与图像模型配置。"""
    conn = pymysql.connect(
        host=db_host,
        port=db_port,
        user=db_user,
        password=db_password,
        database=db_name,
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
    )
    try:
        with conn.cursor() as cur:
            # 1. 商汤渠道
            cur.execute(
                "SELECT id, channel_name, channel_code, protocol, base_url, api_key "
                "FROM ai_channel "
                "WHERE deleted = 0 AND status = 1 AND channel_code = %s "
                "ORDER BY priority ASC, id ASC LIMIT 1",
                (CHANNEL_CODE,),
            )
            channel = cur.fetchone()
            if not channel:
                raise SystemExit(
                    f"[{prompt_no}] 未找到可用的商汤渠道（ai_channel.channel_code={CHANNEL_CODE}）"
                )
            channel_id = channel["id"]
            api_key = channel["api_key"]
            base_url = (channel["base_url"] or "").strip()

            # 2. 该渠道下可用的图片模型
            cur.execute(
                "SELECT model_name, model_code FROM ai_model "
                "WHERE deleted = 0 AND status = 1 AND channel_id = %s AND model_type = %s "
                "ORDER BY sort ASC, id ASC LIMIT 1",
                (channel_id, MODEL_TYPE_IMAGE),
            )
            model = cur.fetchone()
            if not model:
                raise SystemExit(
                    f"[{prompt_no}] 渠道 {channel['channel_name']} 下未找到图片模型（model_type=IMAGE）"
                )
            return {
                "api_key": api_key,
                "base_url": base_url,
                "model_name": model["model_name"],
                "model_code": model["model_code"],
            }
    finally:
        conn.close()


def normalize_base_url(base_url):
    """把渠道 base_url 归一化成图片生成接口地址（同 SenseNovaImageAdapter.buildImagesUrl）。"""
    url = (base_url or "").strip()
    if not url:
        return "https://token.sensenova.cn/v1/images/generations"
    if url.endswith("/"):
        url = url.rstrip("/")
    if url.endswith("/images/generations"):
        return url
    if url.endswith("/chat/completions"):
        return url[: -len("/chat/completions")] + "/images/generations"
    if url.endswith("/v1"):
        return url + "/images/generations"
    return url + "/images/generations"


def generate_image(config, prompt, dry_run):
    """调用商汤图片生成接口，返回图片原始字节。"""
    images_url = normalize_base_url(config["base_url"])
    payload = {
        "model": config["model_code"],
        "prompt": prompt,
        "size": SIZE,
        "n": N,
    }
    headers = {
        "Authorization": "Bearer " + config["api_key"],
        "Content-Type": "application/json",
        "Accept": "application/json",
    }

    if dry_run:
        print("    - [dry-run] 请求体：")
        print("      url   :", images_url)
        print("      model :", payload["model"])
        print("      size  :", payload["size"])
        print("      prompt:", prompt)
        return None

    resp = requests.post(images_url, headers=headers, data=json.dumps(payload), timeout=TIMEOUT)
    if resp.status_code != 200:
        raise RuntimeError(
            f"商汤接口返回 {resp.status_code}：{resp.text[:500]}"
        )
    body = resp.json()

    # 兼容多种字段结构，逐层查找图片（与后端解析思路一致）
    image_data = _extract_image(body)
    if image_data is None:
        raise RuntimeError(f"响应中未解析到图片：{json.dumps(body, ensure_ascii=False)[:500]}")

    if isinstance(image_data, dict):
        image_data = _pick_url_or_b64(image_data)
    elif isinstance(image_data, str) and image_data.startswith("{"):
        image_data = _pick_url_or_b64(json.loads(image_data))
    if not image_data or not isinstance(image_data, str):
        raise RuntimeError("响应中未找到图片 url 或 base64 字段")

    if image_data.startswith("data:"):
        image_data = image_data.split(",", 1)[1]
    if image_data.startswith("http://") or image_data.startswith("https://"):
        pic = requests.get(image_data, timeout=TIMEOUT)
        if pic.status_code != 200:
            raise RuntimeError(f"下载图片失败 {pic.status_code}")
        return pic.content
    # 否则视为 base64
    return base64.b64decode(image_data)


def _extract_image(obj):
    """在响应 JSON 中逐层寻找图片数据。"""
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
    # 叶子：当前对象自身若是图片候选则返回
    if _looks_like_image(obj):
        return obj
    for v in obj.values():
        found = _extract_image(v)
        if found is not None:
            return found
    return None


def _looks_like_image(obj):
    if not isinstance(obj, dict):
        return False
    return _pick_url_or_b64(obj) is not None


def _pick_url_or_b64(obj):
    if not isinstance(obj, dict):
        return None
    for key in ("image_url", "url", "output_url", "origin_image_url", "imageUrl", "outputUrl"):
        val = obj.get(key)
        if val:
            return val
    for key in ("b64_json", "base64", "image_base64", "imageBase64"):
        val = obj.get(key)
        if val:
            return val
    return None


def crop_to_icon(raw, out_scale):
    """委托给通用工具 gen_image.process_icon（彩色 + 背景色填充去水印 + 缩放）。"""
    from gen_image import process_icon
    return process_icon(raw, out_scale, style="color")


def main():
    parser = argparse.ArgumentParser(description="用商汤大模型为元灵工具箱生成 PDF 图标")
    parser.add_argument("--dry-run", action="store_true", help="只打印配置与请求，不真正调用")
    parser.add_argument("--api-key", default=None, help="覆盖读库得到的 api_key")
    parser.add_argument("--db-host", default="127.0.0.1")
    parser.add_argument("--db-port", type=int, default=3306)
    parser.add_argument("--db-user", default="root")
    parser.add_argument("--db-password", default="000000")
    parser.add_argument("--db-name", default="ai_video_studio")
    parser.add_argument("--out-scale", type=int, default=OUTPUT_SCALE, help="输出图标边长(px)")
    args = parser.parse_args()

    print("== 读取 ai_video_studio 商汤渠道配置 ==")
    config = load_channel_config(
        sys.argv[0], args.db_host, args.db_port, args.db_user, args.db_password, args.db_name,
    )
    if args.api_key:
        config["api_key"] = args.api_key
    mask = (config["api_key"][:5] + "****") if config["api_key"] and len(config["api_key"]) > 5 else "****"
    print(f"  渠道     : senzenova")
    print(f"  图片模型 : {config['model_name']} ({config['model_code']})")
    print(f"  base_url : {normalize_base_url(config['base_url'])}")
    print(f"  api_key  : {mask}")
    print(f"  尺寸     : {SIZE}")

    if args.dry_run:
        print("== 预演（不调用） ==")
        for icon in ICONS:
            print(f"\n[{icon['name']}]")
            generate_image(config, icon["prompt"], dry_run=True)
        print("\n[dry-run] 完成。")
        return

    os.makedirs(ASSETS_DIR, exist_ok=True)
    print("== 开始生成图标 ==")
    generated = []
    for idx, icon in enumerate(ICONS, 1):
        print(f"\n[{idx}/{len(ICONS)}] 生成图标：{icon['name']} ...")
        attempts = 0
        raw = None
        while attempts < 3:
            try:
                attempts += 1
                raw = generate_image(config, icon["prompt"], dry_run=False)
                break
            except Exception as exc:  # noqa: BLE001
                print(f"    第 {attempts} 次尝试失败：{exc}")
                if attempts < 3:
                    time.sleep(5)
        if raw is None:
            print(f"    [警告] 图标 {icon['name']} 生成失败，跳过。")
            continue
        processed = crop_to_icon(raw, args.out_scale)
        out_path = os.path.join(ASSETS_DIR, icon["filename"])
        processed.save(out_path, "PNG")
        generated.append((icon["name"], out_path))
        print(f"    已保存：{out_path}（{processed.size[0]}x{processed.size[1]}）")

    print("\n== 完成 ==")
    for name, path in generated:
        print(f"  {name}: {path}")


if __name__ == "__main__":
    main()