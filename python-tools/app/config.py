# -*- coding: utf-8 -*-
"""
应用配置模块
"""
from pathlib import Path

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """应用配置"""

    # 服务配置
    app_name: str = "Python Tools"
    app_version: str = "0.1.0"
    host: str = "0.0.0.0"
    port: int = 8100
    debug: bool = False

    # 文件上传配置
    upload_dir: Path = Path("temp/upload")
    output_dir: Path = Path("temp/output")
    max_upload_size: int = 50 * 1024 * 1024  # 50MB

    # 允许的文件类型
    allowed_pdf_extensions: set[str] = {".pdf"}

    model_config = {"env_prefix": "PYTOOL_", "env_file": ".env"}


settings = Settings()

# 确保临时目录存在
settings.upload_dir.mkdir(parents=True, exist_ok=True)
settings.output_dir.mkdir(parents=True, exist_ok=True)
