# -*- coding: utf-8 -*-
"""
条码/二维码识别服务
识别图片中已有的条码/二维码，返回识别到的原始内容字符串（与业务解耦）。
二维码识别基于 opencv QRCodeDetector；条形码识别基于 pyzbar（需 zbar DLL）。
"""
import logging

import cv2
import numpy as np

logger = logging.getLogger(__name__)

# 允许的图片扩展名
IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp"}

# 最大允许上传 50MB
MAX_UPLOAD_SIZE = 50 * 1024 * 1024


class BarcodeDecodeError(Exception):
    """条码/二维码识别失败"""


def _fast_qr_decode(img_bgr: np.ndarray) -> str | None:
    """
    快速解码单个二维码（单尺度，速度快）
    """
    qr_detector = cv2.QRCodeDetector()
    try:
        data, _, _ = qr_detector.detectAndDecode(img_bgr)
    except cv2.error:
        return None
    return data or None


def _robust_qr_decode(img) -> str | None:
    """
    鲁棒解码：灰度/彩色多尺度检测，提升小图/模糊二维码识别率
    """
    candidates = [img]
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    candidates.append(gray)

    # 上采样两倍，提升小二维码识别率
    up = cv2.resize(img, None, fx=2.0, fy=2.0, interpolation=cv2.INTER_CUBIC)
    candidates.append(up)
    candidates.append(cv2.cvtColor(up, cv2.COLOR_BGR2GRAY))

    qr_detector = cv2.QRCodeDetector()
    for cand in candidates:
        try:
            data, _, _ = qr_detector.detectAndDecode(cand)
        except cv2.error:
            continue
        if data:
            return data
    return None


def _try_decode_barcode(img) -> str | None:
    """
    尝试用 pyzbar 识别条形码/二维码（需要 zbar DLL）。
    若 pyzbar 不可用或未识别到，返回 None。
    """
    try:
        from pyzbar.pyzbar import decode
    except Exception:  # noqa: BLE001 - pyzbar 或 zbar 缺失时为可选能力
        logger.debug("pyzbar 不可用，跳过条形码识别")
        return None
    try:
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        gray_bytes = np.ascontiguousarray(gray)
        results = decode((gray_bytes, gray_bytes.shape[0], gray_bytes.shape[1]))
        if results:
            raw = results[0].data
            if raw:
                try:
                    return raw.decode("utf-8")
                except UnicodeDecodeError:
                    return raw.decode("latin1")
    except Exception:  # noqa: BLE001
        logger.debug("pyzbar 识别异常，忽略")
    return None


def decode_image(file_bytes: bytes, filename: str) -> dict:
    """
    解码图片中的条码/二维码。

    Args:
        file_bytes: 图片二进制内容
        filename: 原始文件名（用于校验扩展名）

    Returns:
        {"content": str} 识别到的原始内容

    Raises:
        BarcodeDecodeError: 校验失败或未识别到条码
    """
    ext = (filename or "unknown.png").lower().split(".")[-1]
    if f".{ext}" not in IMAGE_EXTENSIONS:
        raise BarcodeDecodeError(f"不支持的图片格式: {ext}，仅支持 jpg/jpeg/png/bmp/webp")

    if len(file_bytes) > MAX_UPLOAD_SIZE:
        raise BarcodeDecodeError("图片过大，最大支持 50MB")

    img_arr = np.frombuffer(file_bytes, dtype=np.uint8)
    img = cv2.imdecode(img_arr, cv2.IMREAD_COLOR)
    if img is None:
        raise BarcodeDecodeError("图片解析失败，请确认是有效的图片文件")

    # 优先快速二维码识别，失败做鲁棒识别，最后尝试条形码
    content = _fast_qr_decode(img)
    if not content:
        content = _robust_qr_decode(img)
    if not content:
        content = _try_decode_barcode(img)

    if not content:
        raise BarcodeDecodeError("未在图片中识别到条码或二维码，请正对码、光线充足后重试")

    return {"content": content}