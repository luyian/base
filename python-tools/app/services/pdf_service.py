# -*- coding: utf-8 -*-
"""
PDF 转换服务
"""
import logging
import shutil
import uuid
from pathlib import Path

import pymupdf
import pymupdf4llm
from pdf2docx import Converter

from app.config import settings

logger = logging.getLogger(__name__)


class PdfService:
    """PDF 文件处理服务"""

    # PDF 压缩档位：level -> (dpi_threshold, dpi_target, jpeg_quality)
    COMPRESS_LEVELS = {
        "high": (220, 200, 80),
        "medium": (170, 150, 65),
        "low": (120, 100, 50),
    }

    @staticmethod
    def compress_pdf(input_path: Path, level: str = "medium") -> Path:
        """
        压缩 PDF 文件

        主流三步管线：图像降采样重编码 → 字体子集化 → 结构清理压缩。
        若压缩后体积不小于原文件，则以原文件内容作为输出（不劣化）。

        Args:
            input_path: PDF 文件路径
            level: 压缩档位 high|medium|low

        Returns:
            压缩后的 PDF 文件路径

        Raises:
            RuntimeError: 档位非法或压缩失败时抛出
        """
        if level not in PdfService.COMPRESS_LEVELS:
            raise RuntimeError(f"非法压缩档位: {level}")
        dpi_threshold, dpi_target, quality = PdfService.COMPRESS_LEVELS[level]

        output_filename = f"{input_path.stem}_{uuid.uuid4().hex[:8]}.pdf"
        output_path = settings.output_dir / output_filename

        doc = None
        try:
            doc = pymupdf.open(str(input_path))
            if doc.needs_pass:
                raise RuntimeError("PDF 已加密，无法压缩")
            # 1. 图像降采样 + JPEG 重编码（体积大头）
            doc.rewrite_images(
                dpi_threshold=dpi_threshold, dpi_target=dpi_target, quality=quality
            )
            # 2. 字体子集化（失败不阻断主流程）
            try:
                doc.subset_fonts()
            except Exception:
                logger.warning("字体子集化失败，跳过: %s", input_path.name, exc_info=True)
            # 3. 结构清理：清除未引用对象、压缩对象流
            doc.save(
                str(output_path),
                garbage=4,
                deflate=True,
                clean=True,
                deflate_images=True,
            )
            doc.close()
            doc = None
        except RuntimeError:
            if output_path.exists():
                output_path.unlink(missing_ok=True)
            raise
        except Exception as e:
            logger.exception("PDF 压缩失败: %s", input_path.name)
            if output_path.exists():
                output_path.unlink(missing_ok=True)
            raise RuntimeError(f"PDF 压缩失败: {e!s}") from e
        finally:
            if doc is not None:
                doc.close()

        if not output_path.exists():
            raise RuntimeError("压缩完成但输出文件未生成")

        # 兜底：纯文本型 PDF 压缩空间有限，结果不小于原文件时返回原文件内容
        input_size = input_path.stat().st_size
        if output_path.stat().st_size >= input_size:
            shutil.copyfile(input_path, output_path)
            logger.info("PDF 压缩无增益，返回原文件: %s", input_path.name)

        logger.info(
            "PDF 压缩成功[%s]: %s，%d -> %d bytes",
            level,
            input_path.name,
            input_size,
            output_path.stat().st_size,
        )
        return output_path

    @staticmethod
    def convert_to_word(input_path: Path) -> Path:
        """
        将 PDF 文件转换为 Word 文档

        Args:
            input_path: PDF 文件路径

        Returns:
            生成的 Word 文件路径

        Raises:
            RuntimeError: 转换失败时抛出
        """
        output_filename = f"{input_path.stem}_{uuid.uuid4().hex[:8]}.docx"
        output_path = settings.output_dir / output_filename

        try:
            cv = Converter(str(input_path))
            cv.convert(str(output_path))
            cv.close()
        except Exception as e:
            logger.exception("PDF 转 Word 失败: %s", input_path.name)
            # 清理可能生成的残留文件
            if output_path.exists():
                output_path.unlink(missing_ok=True)
            raise RuntimeError(f"PDF 转换失败: {e!s}") from e

        if not output_path.exists():
            raise RuntimeError("转换完成但输出文件未生成")

        logger.info("PDF 转 Word 成功: %s -> %s", input_path.name, output_filename)
        return output_path

    @staticmethod
    def convert_to_markdown(input_path: Path) -> Path:
        """
        将 PDF 文件转换为 Markdown 文档

        Args:
            input_path: PDF 文件路径

        Returns:
            生成的 Markdown 文件路径

        Raises:
            RuntimeError: 转换失败时抛出
        """
        output_filename = f"{input_path.stem}_{uuid.uuid4().hex[:8]}.md"
        output_path = settings.output_dir / output_filename

        try:
            md_text = pymupdf4llm.to_markdown(str(input_path))
            output_path.write_text(md_text, encoding="utf-8")
        except Exception as e:
            logger.exception("PDF 转 Markdown 失败: %s", input_path.name)
            if output_path.exists():
                output_path.unlink(missing_ok=True)
            raise RuntimeError(f"PDF 转 Markdown 失败: {e!s}") from e

        if not output_path.exists():
            raise RuntimeError("转换完成但输出文件未生成")

        logger.info("PDF 转 Markdown 成功: %s -> %s", input_path.name, output_filename)
        return output_path

    @staticmethod
    def validate_file(filename: str, file_size: int) -> str | None:
        """
        校验上传文件

        Args:
            filename: 文件名
            file_size: 文件大小(字节)

        Returns:
            错误信息，None 表示校验通过
        """
        suffix = Path(filename).suffix.lower()
        if suffix not in settings.allowed_pdf_extensions:
            return f"不支持的文件类型: {suffix}，仅支持 PDF 文件"

        if file_size > settings.max_upload_size:
            max_mb = settings.max_upload_size // (1024 * 1024)
            return f"文件大小超限，最大允许 {max_mb}MB"

        return None


pdf_service = PdfService()
