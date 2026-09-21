# -*- coding: utf-8 -*-
"""
条码/二维码识别路由
"""
from fastapi import APIRouter, File, UploadFile

from app.schema import Result
from app.services import barcode_service

router = APIRouter()


@router.post("/decode-image", summary="识别图片中的条码/二维码")
async def decode_image(file: UploadFile = File(...)):
    """
    上传一张含条码/二维码的图片，返回识别到的原始内容。

    - 识别优先级：二维码(cv2) → 条形码(pyzbar，需 zbar DLL)
    - 仅返回识别到的原始内容字符串，不判断业务命中逻辑（由调用方决定）
    - 未识别到返回 400 中文错误
    """
    content = await file.read()
    try:
        result = barcode_service.decode_image(content, file.filename or "unknown.png")
        return Result.ok(data=result)
    except barcode_service.BarcodeDecodeError as e:
        return Result.fail(message=str(e), code=400)
    except Exception as e:  # noqa: BLE001
        return Result.fail(message=f"识别服务异常: {e!s}")