# -*- coding: utf-8 -*-
"""
PDF 转换服务
"""
import logging
import uuid
from pathlib import Path

import pymupdf4llm
from pdf2docx import Converter

from app.config import settings

logger = logging.getLogger(__name__)


class PdfService:
    """PDF 文件处理服务"""

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
