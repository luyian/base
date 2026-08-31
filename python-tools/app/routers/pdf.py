# -*- coding: utf-8 -*-
"""
PDF 工具路由
"""
import uuid
from pathlib import Path

from fastapi import APIRouter, File, Form, UploadFile
from fastapi.responses import FileResponse, JSONResponse

from app.config import settings
from app.schema import Result
from app.services.pdf_service import pdf_service
from app.services.scan_service import scan_service

router = APIRouter()


@router.post("/to-word", summary="PDF 转 Word")
async def pdf_to_word(file: UploadFile) -> FileResponse:
    """
    上传 PDF 文件，转换为 Word 文档后返回下载

    - 最大支持 50MB
    - 返回 .docx 文件流
    """
    # 校验文件
    content = await file.read()
    error = pdf_service.validate_file(file.filename or "unknown.pdf", len(content))
    if error:
        from fastapi.responses import JSONResponse
        return JSONResponse(  # type: ignore[return-value]
            status_code=400,
            content=Result.fail(message=error, code=400).model_dump(),
        )

    # 保存临时文件
    temp_filename = f"{uuid.uuid4().hex}.pdf"
    temp_path = settings.upload_dir / temp_filename

    try:
        temp_path.write_bytes(content)

        # 执行转换
        output_path = pdf_service.convert_to_word(temp_path)

        # 返回文件下载
        download_name = Path(file.filename or "output").stem + ".docx"
        return FileResponse(
            path=str(output_path),
            filename=download_name,
            media_type="application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        )
    except RuntimeError as e:
        from fastapi.responses import JSONResponse
        return JSONResponse(  # type: ignore[return-value]
            status_code=500,
            content=Result.fail(message=str(e)).model_dump(),
        )
    finally:
        # 清理上传的临时文件
        temp_path.unlink(missing_ok=True)


@router.post("/to-markdown", summary="PDF 转 Markdown")
async def pdf_to_markdown(file: UploadFile) -> FileResponse:
    """
    上传 PDF 文件，转换为 Markdown 文档后返回下载

    - 最大支持 50MB
    - 返回 .md 文件流
    """
    # 校验文件
    content = await file.read()
    error = pdf_service.validate_file(file.filename or "unknown.pdf", len(content))
    if error:
        from fastapi.responses import JSONResponse
        return JSONResponse(  # type: ignore[return-value]
            status_code=400,
            content=Result.fail(message=error, code=400).model_dump(),
        )

    # 保存临时文件
    temp_filename = f"{uuid.uuid4().hex}.pdf"
    temp_path = settings.upload_dir / temp_filename

    try:
        temp_path.write_bytes(content)

        # 执行转换
        output_path = pdf_service.convert_to_markdown(temp_path)

        # 返回文件下载
        download_name = Path(file.filename or "output").stem + ".md"
        return FileResponse(
            path=str(output_path),
            filename=download_name,
            media_type="text/markdown; charset=utf-8",
        )
    except RuntimeError as e:
        from fastapi.responses import JSONResponse
        return JSONResponse(  # type: ignore[return-value]
            status_code=500,
            content=Result.fail(message=str(e)).model_dump(),
        )
    finally:
        # 清理上传的临时文件
        temp_path.unlink(missing_ok=True)


@router.post("/compress", summary="PDF 压缩")
async def pdf_compress(
    file: UploadFile,
    level: str = Form("medium", description="压缩档位 high|medium|low"),
) -> FileResponse:
    """
    上传 PDF 文件，按档位压缩后返回下载

    - 压缩管线：图像降采样重编码 → 字体子集化 → 结构清理
    - 压缩后体积不小于原文件时返回原文件（不劣化）
    - 最大支持 50MB，返回 application/pdf 文件流
    """
    # 校验文件
    content = await file.read()
    error = pdf_service.validate_file(file.filename or "unknown.pdf", len(content))
    if error:
        return JSONResponse(  # type: ignore[return-value]
            status_code=400,
            content=Result.fail(message=error, code=400).model_dump(),
        )
    if level not in pdf_service.COMPRESS_LEVELS:
        return JSONResponse(  # type: ignore[return-value]
            status_code=400,
            content=Result.fail(
                message=f"非法压缩档位: {level}，仅支持 high/medium/low", code=400
            ).model_dump(),
        )

    # 保存临时文件
    temp_filename = f"{uuid.uuid4().hex}.pdf"
    temp_path = settings.upload_dir / temp_filename

    try:
        temp_path.write_bytes(content)

        # 执行压缩
        output_path = pdf_service.compress_pdf(temp_path, level)

        # 返回文件下载
        download_name = Path(file.filename or "output").stem + ".pdf"
        return FileResponse(
            path=str(output_path),
            filename=download_name,
            media_type="application/pdf",
        )
    except RuntimeError as e:
        return JSONResponse(  # type: ignore[return-value]
            status_code=500,
            content=Result.fail(message=str(e)).model_dump(),
        )
    finally:
        # 清理上传的临时文件
        temp_path.unlink(missing_ok=True)


@router.post("/images-to-pdf", summary="多张图片压缩合并为 PDF")
async def images_to_pdf(
    files: list[UploadFile] = File(..., description="图片文件列表，顺序即 PDF 页序"),
    max_side: int = Form(2000, ge=800, le=4000, description="降采样后最长边像素上限"),
    quality: int = Form(70, ge=20, le=95, description="JPEG 压缩质量(0-100)"),
) -> FileResponse:
    """
    上传多张文档扫描图片，按顺序压缩并合并成一份 PDF

    - 压缩管线：EXIF 摆正 → 灰度化 → LANCZOS 降采样 → JPEG 有损
    - max_side 与 quality 兼顾体积与清晰度（常规取 2000/70）
    - 返回 application/pdf 文件流
    """
    temp_paths: list[Path] = []
    try:
        # 1. 校验并落盘每张上传图片
        for file in files:
            content = await file.read()
            error = scan_service.validate_image(file.filename or "unknown.png", len(content))
            if error:
                return JSONResponse(  # type: ignore[return-value]
                    status_code=400,
                    content=Result.fail(message=error, code=400).model_dump(),
                )
            suffix = Path(file.filename or "unknown.png").suffix.lower()
            temp_path = settings.upload_dir / f"{uuid.uuid4().hex}{suffix}"
            temp_path.write_bytes(content)
            temp_paths.append(temp_path)

        if not temp_paths:
            return JSONResponse(  # type: ignore[return-value]
                status_code=400,
                content=Result.fail(message="未收到任何图片", code=400).model_dump(),
            )

        # 2. 压缩合并为 PDF
        output_path = scan_service.images_to_pdf(temp_paths, max_side=max_side, quality=quality)

        # 3. 返回文件下载
        return FileResponse(
            path=str(output_path),
            filename="output.pdf",
            media_type="application/pdf",
        )
    except RuntimeError as e:
        return JSONResponse(  # type: ignore[return-value]
            status_code=500,
            content=Result.fail(message=str(e)).model_dump(),
        )
    finally:
        # 清理上传的临时文件（压缩中间产物已由 service 内部清理）
        for temp_path in temp_paths:
            temp_path.unlink(missing_ok=True)
