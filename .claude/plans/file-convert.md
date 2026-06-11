# 文件转换功能实现计划

## 概述
在系统管理下新增"文件转换"菜单，首先实现 PDF 转 Word 功能，架构上预留扩展位置（后续可加 Word 转 PDF、图片转 PDF 等）。

## 实现步骤

### 1. 后端 - FileConvertController
路径：`backend/src/main/java/com/base/system/controller/FileConvertController.java`

- `POST /system/file-convert/pdf-to-word` — 接收 PDF 文件，调用 python-tools 转换，返回 docx 文件流
- 内部通过 RestTemplate 向 `http://localhost:8100/api/pdf/to-word` 发起 multipart 请求
- 配置复用 `ai.skill.python-tools-url`（已有的 python-tools 地址配置）

### 2. 后端 - FileConvertService
路径：`backend/src/main/java/com/base/system/service/FileConvertService.java`

- `byte[] convertPdfToWord(MultipartFile file)` — 封装 HTTP 调用逻辑
- 后续新增转换类型只需添加新方法

### 3. 前端 - API 层
路径：`frontend/src/api/fileConvert.js`

- `pdfToWord(file)` — multipart 上传，responseType: blob

### 4. 前端 - 页面组件
路径：`frontend/src/views/system/FileConvert.vue`

- 顶部 Tab 切换不同转换类型（当前仅 "PDF → Word"，预留 Tab 位置）
- 拖拽/点击上传 PDF 文件
- 点击"开始转换"按钮
- 转换完成后自动触发下载

### 5. 菜单权限 SQL
路径：`backend/src/main/resources/db/init_file_convert_permission.sql`

- 在系统管理（parent_id=1）下新增菜单，ID=114
- 组件路径 `system/FileConvert`，路由 `/file-convert`
- 分配给管理员角色

## 文件清单
1. `backend/.../controller/FileConvertController.java`
2. `backend/.../service/FileConvertService.java`
3. `backend/.../service/impl/FileConvertServiceImpl.java`
4. `frontend/src/api/fileConvert.js`
5. `frontend/src/views/system/FileConvert.vue`
6. `backend/.../db/init_file_convert_permission.sql`
