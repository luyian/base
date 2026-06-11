# Python Tools

Python 工具服务，基于 FastAPI 构建，对外提供 HTTP 接口供 Spring Boot 后端调用。

## 环境要求

- Python >= 3.10
- pip

## 快速开始（开发环境）

```bash
cd python-tools

# 创建虚拟环境
python -m venv venv
venv\Scripts\activate        # Windows
# source venv/bin/activate   # Linux/Mac

# 安装依赖
pip install -r requirements.txt

# 启动服务
python start.py
```

Windows 可直接双击 `run.bat`，首次自动创建虚拟环境并安装依赖。

服务启动后访问：
- API 文档：http://localhost:8100/docs
- 健康检查：http://localhost:8100/health

## 生产部署

### 方式一：直接部署

```bash
cd python-tools

# 创建虚拟环境并安装依赖
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# 生产启动（关闭 debug，指定 workers）
PYTOOL_DEBUG=false uvicorn app.main:app --host 0.0.0.0 --port 8100 --workers 2
```

建议配合 systemd 管理进程：

```ini
# /etc/systemd/system/python-tools.service
[Unit]
Description=Python Tools Service
After=network.target

[Service]
Type=simple
User=app
WorkingDirectory=/opt/python-tools
ExecStart=/opt/python-tools/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8100 --workers 2
Restart=always
RestartSec=5
Environment=PYTOOL_DEBUG=false

[Install]
WantedBy=multi-user.target
```

```bash
systemctl enable python-tools
systemctl start python-tools
```

### 方式二：Docker 部署

```dockerfile
FROM python:3.12-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

COPY app/ ./app/
COPY start.py .

EXPOSE 8100
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8100", "--workers", "2"]
```

```bash
docker build -t python-tools .
docker run -d --name python-tools -p 8100:8100 python-tools
```

### Spring Boot 端配置

后端通过以下配置连接本服务：

```yaml
# application-dev.yml
ai:
  skill:
    python-tools-url: http://localhost:8100

# application-prod.yml（如部署在同一台机器）
ai:
  skill:
    python-tools-url: http://127.0.0.1:8100
```

**启动顺序**：先启动 python-tools，再启动 Spring Boot 后端。

## 接口列表

### PDF 工具

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/pdf/to-word` | PDF 转 Word（multipart 上传，返回 docx 文件流） |

### 股票数据

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/stock/quote?codes=600519,000858` | 股票实时行情 |
| GET | `/api/stock/fund-flow?code=600519` | 个股资金流向 |
| GET | `/api/stock/news?code=600519` | 个股新闻 |
| GET | `/api/stock/industry-rank` | 行业板块排名 |
| GET | `/api/stock/northbound-flow` | 北向资金流向 |
| GET | `/api/stock/dragon-tiger?code=002475` | 龙虎榜数据 |
| GET | `/api/stock/hot-stocks` | 当日强势股 |
| GET | `/api/stock/concept-blocks?code=600519` | 板块归属 |
| GET | `/api/stock/margin-trading?code=600519` | 融资融券 |
| GET | `/api/stock/valuation?code=688017` | 综合估值 |

### 通用

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/health` | 健康检查 |

## 配置说明

通过 `.env` 文件或环境变量配置，前缀 `PYTOOL_`：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `PYTOOL_HOST` | 0.0.0.0 | 监听地址 |
| `PYTOOL_PORT` | 8100 | 监听端口 |
| `PYTOOL_DEBUG` | false | 调试模式（热重载） |
| `PYTOOL_MAX_UPLOAD_SIZE` | 52428800 | 最大上传大小(字节) |

## 项目结构

```
python-tools/
├── app/
│   ├── main.py              # FastAPI 入口
│   ├── config.py            # 配置管理
│   ├── schema.py            # 统一响应模型
│   ├── routers/
│   │   ├── pdf.py           # PDF 工具接口
│   │   └── stock.py         # 股票数据接口
│   └── services/
│       ├── em_helper.py     # 东财公共模块（限流+会话）
│       ├── pdf_service.py   # PDF 转换逻辑
│       └── stock_service.py # 股票数据查询
├── temp/                    # 临时文件（自动创建，gitignore）
├── requirements.txt         # 依赖清单
├── pyproject.toml           # 项目配置（ruff/mypy/pytest）
├── start.py                 # 启动脚本
├── run.bat                  # Windows 一键启动
└── .env                     # 环境变量
```

## 扩展新功能

1. 在 `app/services/` 下新建 `xxx_service.py`
2. 在 `app/routers/` 下新建 `xxx.py` 定义路由
3. 在 `app/main.py` 中 `include_router` 注册
