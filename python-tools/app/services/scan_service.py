# -*- coding: utf-8 -*-
"""
文档扫描图片整理服务：多张图片压缩后合并为 PDF

压缩管线（面向手机拍摄大图，灰度优先）：
1. ImageOps.exif_transpose 摆正方向（手机图常带 EXIF orientation）
2. convert("L") 灰度化（文档类图体积大幅下降）
3. thumbnail + LANCZOS 高质量降采样（限最长边、不放大、保比例）
4. save JPEG(quality, optimize, progressive) 有损压缩
5. PyMuPDF(fitz) 逐图整页插入合成为 PDF
"""
import logging
import uuid
from pathlib import Path

import fitz  # PyMuPDF
from PIL import Image, ImageOps

from app.config import settings

logger = logging.getLogger(__name__)

# A4 纸尺寸（pt）：210mm × 297mm
A4_WIDTH = 595.28
A4_HEIGHT = 841.89


class ScanService:
    """文档扫描图片 → PDF 服务"""

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
    def images_to_pdf(image_paths: list[Path], max_side: int = 2000, quality: int = 70) -> Path:
        """
        将多张图片压缩后合并为 PDF（图片顺序即页序）

        Args:
            image_paths: 源图片路径列表（有序）
            max_side: 降采样后最长边像素上限，默认 2000
            quality: JPEG 压缩质量(0-100)，默认 70

        Returns:
            生成的 PDF 文件路径

        Raises:
            RuntimeError: 处理失败时抛出
        """
        output_filename = f"scan_{uuid.uuid4().hex[:8]}.pdf"
        output_path = settings.output_dir / output_filename
        jpeg_paths: list[Path] = []

        try:
            # 1. 逐图压缩为灰度 JPEG
            for index, src in enumerate(image_paths):
                jpeg_path = ScanService._compress_image(src, index, max_side, quality)
                jpeg_paths.append(jpeg_path)

            # 2. 合并为 PDF
            pdf_doc = fitz.Document()  # type: ignore[attr-defined]
            try:
                for jpeg_path in jpeg_paths:
                    pix = fitz.Pixmap(str(jpeg_path))  # type: ignore[attr-defined]
                    try:
                        # 每页固定 A4 尺寸（210mm × 297mm）
                        page = pdf_doc.new_page(width=A4_WIDTH, height=A4_HEIGHT)
                        # 图片按比例缩放放入页面并居中，避免拉伸变形，四周留白
                        scale = min(A4_WIDTH / pix.width, A4_HEIGHT / pix.height)
                        dw, dh = pix.width * scale, pix.height * scale
                        rect = fitz.Rect(  # type: ignore[attr-defined]
                            (A4_WIDTH - dw) / 2, (A4_HEIGHT - dh) / 2,
                            (A4_WIDTH + dw) / 2, (A4_HEIGHT + dh) / 2,
                        )
                        page.insert_image(rect, filename=str(jpeg_path))
                    finally:
                        pix = None
                pdf_doc.save(str(output_path), garbage=4, deflate=True)
            finally:
                pdf_doc.close()
        except Exception as e:
            logger.exception("图片合并 PDF 失败")
            if output_path.exists():
                output_path.unlink(missing_ok=True)
            raise RuntimeError(f"图片转 PDF 失败: {e!s}") from e
        finally:
            # 清理压缩中间产物（JPEG）
            for jpeg_path in jpeg_paths:
                jpeg_path.unlink(missing_ok=True)

        if not output_path.exists():
            raise RuntimeError("合并完成但输出 PDF 未生成")

        logger.info(
            "图片合并 PDF 成功: %s 张 -> %s (%.2f KB)",
            len(image_paths), output_filename, output_path.stat().st_size / 1024,
        )
        return output_path

    @staticmethod
    def _compress_image(src: Path, index: int, max_side: int, quality: int) -> Path:
        """
        单张图片压缩为灰度 JPEG

        Args:
            src: 源图片路径
            index: 序号（用于命名中间文件，保证顺序）
            max_side: 降采样后最长边像素上限
            quality: JPEG 质量

        Returns:
            压缩后的 JPEG 临时文件路径
        """
        jpeg_path = settings.upload_dir / f"{uuid.uuid4().hex}_{index:04d}.jpg"
        with Image.open(src) as im:
            # 摆正 EXIF 方向
            im = ImageOps.exif_transpose(im)
            # 灰度优先
            im = im.convert("L")
            # 高质量降采样（不放大、保比例）
            im.thumbnail((max_side, max_side), Image.Resampling.LANCZOS)
            # 有损压缩
            im.save(jpeg_path, format="JPEG", quality=quality, optimize=True, progressive=True)
        return jpeg_path


scan_service = ScanService()