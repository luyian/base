@echo off
chcp 65001 >nul
cd /d %~dp0

if not exist venv (
    echo 正在创建虚拟环境...
    python -m venv venv
    call venv\Scripts\activate.bat
    echo 正在安装依赖...
    pip install -r requirements.txt
) else (
    call venv\Scripts\activate.bat
)

echo 启动 Python Tools 服务 (端口 8100)...
python start.py
pause
