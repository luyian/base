# -*- coding: utf-8 -*-
"""
统一响应模型
"""
from typing import Any

from pydantic import BaseModel


class Result(BaseModel):
    """统一响应格式，与后端 Result<T> 保持一致"""

    code: int = 200
    message: str = "success"
    data: Any = None

    @classmethod
    def ok(cls, data: Any = None, message: str = "success") -> "Result":
        """成功响应"""
        return cls(code=200, message=message, data=data)

    @classmethod
    def fail(cls, message: str = "操作失败", code: int = 500) -> "Result":
        """失败响应"""
        return cls(code=code, message=message, data=None)
