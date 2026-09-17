# -*- coding: utf-8 -*-
"""
图片去水印服务

基于 remove-ai-watermarks（cv2 后端，CPU 可跑）封装：
- region 为空：visible --mark auto —— 自动识别并擦除已知 AI 平台水印
  （含 doubao/即梦/千问/可灵/百度/元宝 等，均为右下角）。
- region 有值：erase --region x,y,w,h —— 手动框选任意区域擦除（可覆盖清单外水印/logo）。

依赖：服务器 python-tools 环境需 `pip install "remove-ai-watermarks[visible]"`。
"""
import logging
import os
import shutil
import subprocess
import sys
import uuid
from pathlib import Path

from app.config import settings

logger = logging.getLogger(__name__)

# CLI 可执行文件名（Windows 加 .exe 后缀）
CLI_NAME = "remove-ai-watermarks" + (".exe" if os.name == "nt" else "")


def _find_cli() -> str | None:
    """定位 remove-ai-watermarks 可执行文件。

    优先按 PATH 查找；找不到时回退到当前解释器所在 venv 的
    Scripts(Windows)/bin(Linux) 目录，保证本机与服务器 python-tools 虚拟环境
    都能命中同环境内安装的 CLI。
    """
    exe = shutil.which("remove-ai-watermarks")
    if exe:
        return exe
    base = Path(sys.prefix)
    for sub in ("Scripts", "bin"):
        cand = base / sub / CLI_NAME
        if cand.is_file():
            return str(cand)
    return None


class WatermarkService:
    """图片去水印服务"""

    @staticmethod
    def validate_image(filename: str, file_size: int) -> str | None:
        """
        校验上传图片

        Args:
            filename: 文件名
            file_size: 文件大小(字节)

        Returns:
            错误信息，None 表示校验通过
        """
        suffix = Path(filename).suffix.lower()
        if suffix not in settings.allowed_image_extensions:
            return f"不支持的文件类型: {suffix}，仅支持图片(JPG/PNG/BMP/WebP)"

        if file_size > settings.max_image_size:
            max_mb = settings.max_image_size // (1024 * 1024)
            return f"文件大小超限，单张最大允许 {max_mb}MB"

        return None

    @staticmethod
    def remove_watermark(src: Path, region: str | None, backend: str = "cv2") -> Path:
        """
        去除图片水印

        Args:
            src: 源图片路径
            region: 手动框选区域 "x,y,w,h"，为空则自动识别已知水印
            backend: 擦除后端(cv2/migan/lama)，默认 cv2

        Returns:
            处理后的图片路径

        Raises:
            RuntimeError: 工具缺失或 erase 处理失败
        """
        cli = _find_cli()
        if not cli:
            raise RuntimeError(
                "未找到 remove-ai-watermarks 可执行文件，请安装: pip install \"remove-ai-watermarks[visible]\""
            )

        out = settings.output_dir / f"wm_{uuid.uuid4().hex[:8]}{src.suffix.lower() or '.png'}"
        if region:
            # 手动框选擦除：任意水印/logo，若失败视为真实故障
            cmd = [
                cli, "erase", str(src),
                "--region", region,
                "--backend", backend,
                "--strip-metadata",
                "-o", str(out),
            ]
        else:
            # 自动识别并擦除已知 AI 平台水印（右下角）
            cmd = [
                cli, "visible", str(src),
                "--mark", "auto",
                "--backend", backend,
                "--strip-metadata",
                "-o", str(out),
            ]

        logger.info("调用去水印: %s", " ".join(cmd))
        try:
            result = subprocess.run(cmd, capture_output=True, text=True)
        except Exception as e:
            out.unlink(missing_ok=True)
            raise RuntimeError(f"去水印工具调用失败: {e!s}") from e

        if out.exists() and out.stat().st_size > 0:
            logger.info("去水印成功: %s (%.2f KB)", out.name, out.stat().st_size / 1024)
            return out

        # 无输出产物
        out.unlink(missing_ok=True)
        if region:
            # erase 手动擦除未产出，视为真实失败并向调用方透传
            detail = (result.stderr or "")[:300].strip()
            raise RuntimeError(f"去水印失败(erase): 退出码 {result.returncode} {detail}")

        # visible 自动识别未命中已知水印（干净/未知水印图），回退返回原图，不下故障
        logger.info("未识别到已知可见水印，返回原图: %s", src.name)
        shutil.copyfile(src, out)
        return out


watermark_service = WatermarkService()