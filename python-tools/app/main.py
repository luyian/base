# -*- coding: utf-8 -*-
"""
FastAPI 应用入口
"""
from contextlib import asynccontextmanager
from collections.abc import AsyncGenerator

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app.config import settings
from app.routers import pdf, stock
from app.schema import Result


@asynccontextmanager
async def lifespan(application: FastAPI) -> AsyncGenerator[None, None]:
    """应用生命周期管理"""
    # 启动时执行
    settings.upload_dir.mkdir(parents=True, exist_ok=True)
    settings.output_dir.mkdir(parents=True, exist_ok=True)
    yield
    # 关闭时清理临时文件
    for temp_dir in [settings.upload_dir, settings.output_dir]:
        for file in temp_dir.iterdir():
            if file.is_file():
                file.unlink(missing_ok=True)


app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """全局异常处理"""
    return JSONResponse(
        status_code=500,
        content=Result.fail(message=f"服务内部错误: {exc!s}").model_dump(),
    )


@app.get("/health", summary="健康检查")
async def health_check() -> Result:
    """健康检查接口，供后端探活使用"""
    return Result.ok(data={"status": "running", "version": settings.app_version})


# 注册路由
app.include_router(pdf.router, prefix="/api/pdf", tags=["PDF工具"])
app.include_router(stock.router, prefix="/api/stock", tags=["股票数据"])
