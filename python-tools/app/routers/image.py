# -*- coding: utf-8 -*-
"""
图片工具路由
"""
import uuid
from pathlib import Path

from fastapi import APIRouter, File, Form, UploadFile
from fastapi.responses import FileResponse, JSONResponse

from app.config import settings
from app.schema import Result
from app.services.watermark_service import watermark_service

router = APIRouter()

# 图片扩展名 → Content-Type（用于响应下载名与内嵌预览）
IMAGE_MEDIA_TYPES = {
    ".jpg": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".png": "image/png",
    ".bmp": "image/bmp",
    ".webp": "image/webp",
}


@router.post("/remove-watermark", summary="去除图片水印")
async def remove_watermark(
    file: UploadFile,
    region: str | None = Form(None, description="手动框选区域 x,y,w,h，为空则自动识别已知水印"),
    backend: str = Form("cv2", description="擦除后端 cv2/migan/lama"),
) -> FileResponse:
    """
    上传图片，去除右下角 AI 平台水印后返回下载

    - region 为空：自动识别并擦除豆包/即梦/千问/可灵/百度/元宝 等已知水印
    - region 提供 x,y,w,h：手动框选任意区域擦除
    - 未识别到已知水印时返回原图（不下故障）
    - 最大支持 50MB，返回图片文件流
    """
    content = await file.read()
    error = watermark_service.validate_image(file.filename or "unknown.png", len(content))
    if error:
        return JSONResponse(  # type: ignore[return-value]
            status_code=400,
            content=Result.fail(message=error, code=400).model_dump(),
        )

    suffix = Path(file.filename or "unknown.png").suffix.lower()
    temp_path = settings.upload_dir / f"{uuid.uuid4().hex}{suffix}"

    try:
        temp_path.write_bytes(content)
        output_path = watermark_service.remove_watermark(temp_path, region, backend)

        download_name = Path(file.filename or "output").stem + "_clean" + suffix
        media_type = IMAGE_MEDIA_TYPES.get(suffix, "application/octet-stream")
        return FileResponse(
            path=str(output_path),
            filename=download_name,
            media_type=media_type,
        )
    except RuntimeError as e:
        return JSONResponse(  # type: ignore[return-value]
            status_code=500,
            content=Result.fail(message=str(e)).model_dump(),
        )
    finally:
        # 清理上传的临时文件
        temp_path.unlink(missing_ok=True)