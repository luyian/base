# Python Tools

Python 工具服务，基于 FastAPI 构建，对外提供 HTTP 接口供 Spring Boot 后端调用。

## 环境要求

- Python >= 3.10
- pip

## 快速开始

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

服务启动后访问：
- API 文档：http://localhost:8100/docs
- 健康检查：http://localhost:8100/health

## 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/health` | 健康检查 |
| POST | `/api/pdf/to-word` | PDF 转 Word（multipart 上传） |

## PDF 转 Word 调用示例

```bash
curl -X POST http://localhost:8100/api/pdf/to-word \
  -F "file=@test.pdf" \
  -o output.docx
```

Spring Boot 端调用示例（RestTemplate）：

```java
Resource resource = new FileSystemResource(pdfFile);
MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("file", resource);

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.MULTIPART_FORM_DATA);

HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
byte[] docx = restTemplate.postForObject(
    "http://localhost:8100/api/pdf/to-word", request, byte[].class
);
```

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
│   ├── main.py          # FastAPI 入口
│   ├── config.py        # 配置管理
│   ├── schema.py        # 统一响应模型
│   ├── routers/         # 路由层
│   │   └── pdf.py       # PDF 相关接口
│   └── services/        # 服务层
│       └── pdf_service.py
├── temp/                # 临时文件（自动创建，gitignore）
├── requirements.txt     # 依赖清单
├── pyproject.toml       # 项目配置（ruff/mypy/pytest）
├── start.py             # 启动脚本
└── .env                 # 环境变量
```

## 扩展新功能

1. 在 `app/services/` 下新建 `xxx_service.py`
2. 在 `app/routers/` 下新建 `xxx.py` 定义路由
3. 在 `app/main.py` 中 `include_router` 注册
