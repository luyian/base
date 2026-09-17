"""AI 生成图右下角水印去除（Doubao/即梦/千问/可灵/百度/元宝 等）。

基于 remove-ai-watermarks(visible 模式)封装：
- visible：自动识别并擦除已知 AI 平台水印（--mark auto 覆盖 doubao/jimeng/qwen/kling/
          yuanbao/baidu 等，均在右下角），CPU 上 --backend cv2 即可跑，不下载模型；
          若对效果不满意可换 migan(约 1GB ONNX) 质量更好。
- erase：水印不在已知清单、或自动检测漏掉时，用 --region x,y,w,h 手动框选擦除。
  可多次传 --region 擦除多个区域。

依赖：独立虚拟环境 .venv（已安装 remove-ai-watermarks[visible]）。

用法：
    .venv/Scripts/python.exe -X utf8 remove_watermark_v2.py --in a.png --out a_clean.png
    .venv/Scripts/python.exe -X utf8 remove_watermark_v2.py --in a.png          # 默认 *_clean.png
    .venv/Scripts/python.exe -X utf8 remove_watermark_v2.py --in a.png --backend migan
    .venv/Scripts/python.exe -X utf8 remove_watermark_v2.py --in a.png --region 800,2000,300,90
"""
import argparse
import subprocess
import sys
from pathlib import Path

# 虚拟环境内的 CLI 可执行文件（与脚本同目录的 .venv）
VENV_CLI = Path(__file__).resolve().parent / ".venv" / "Scripts" / "remove-ai-watermarks.exe"


def run_cli(args: list) -> None:
    """调用底层 remove-ai-watermarks 命令并打印其输出。"""
    cmd = [str(VENV_CLI)] + args
    print(">>>", " ".join(cmd))
    try:
        subprocess.run(cmd, check=True)
    except subprocess.CalledProcessError as e:
        print(f"去水印失败，退出码 {e.returncode}", file=sys.stderr)
        raise SystemExit(1)


def main() -> None:
    parser = argparse.ArgumentParser(
        description="去 AI 生成图右下角水印（Doubao/即梦/千问/可灵/百度/元宝）",
        epilog="未命中已知水印时用 --region x,y,w,h 手动框选，可多次传来擦除多个区域。",
    )
    parser.add_argument("--in", dest="src", required=True, help="输入图片路径")
    parser.add_argument("--out", dest="dst", default=None, help="输出图片路径（默认 *_clean.扩展名）")
    parser.add_argument("--backend", choices=["cv2", "migan", "lama"], default="cv2",
                        help="擦除后端，cv2 轻量(默认)，migan 约1GB质量更好，lama 约4.7GB质量最好")
    parser.add_argument("--mark", default="auto",
                        help="已知水印名，默认 auto 自动识别全部")
    parser.add_argument("--region", action="append", metavar="x,y,w,h",
                        help="手动框选要擦除的区域，可多次，例如 800,2000,300,90")
    parser.add_argument("--dilate", type=int, default=2,
                        help="擦除边界向外扩张像素，覆盖水印边缘残留")
    parser.add_argument("--keep-metadata", action="store_true", help="保留 AI 生成元数据（默认去除）")
    args = parser.parse_args()

    base = [args.src]
    if args.dst:
        base += ["-o", args.dst]
    if not args.keep_metadata:
        base.append("--strip-metadata")

    if args.region:
        # 手动框选擦除：不依赖已知水印清单
        for region in args.region:
            base += ["--region", region]
        if args.backend:
            base += ["--backend", args.backend]
        if args.dilate:
            base += ["--dilate", str(args.dilate)]
        run_cli(["erase"] + base)
    else:
        # 自动识别并擦除已知水印
        run_cli(["visible"] + base + ["--mark", args.mark, "--backend", args.backend])

    out = args.dst
    if not out:
        p = Path(args.src)
        out = str(p.with_name(p.stem + "_clean" + p.suffix))
    print(f"已输出: {out}")


if __name__ == "__main__":
    main()