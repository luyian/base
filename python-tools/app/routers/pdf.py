# -*- coding: utf-8 -*-
"""
PDF 工具路由
"""
import uuid
from pathlib import Path

from fastapi import APIRouter, UploadFile
from fastapi.responses import FileResponse

from app.config import settings
from app.schema import Result
from app.services.pdf_service import pdf_service

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
