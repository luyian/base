# PDF 压缩功能 - 归档记录

## 基本信息
- 提案日期: 2026-08-31
- 完成日期: 2026-08-31
- 状态: ✅ 已完成

## 变更摘要

文件转换模块新增「PDF 压缩」功能：前端「PDF 转换」页签输出格式新增「压缩 PDF (.pdf)」选项及三档压缩选择，经后端转发至 python-tools，采用 PyMuPDF 主流三步压缩管线（图像降采样重编码 → 字体子集化 → 结构清理），压缩前后文件均上传 COS 并记录 sys_file，结果页展示压缩率。

## 涉及文件
- 新增: 无
- 修改:
  - `python-tools/app/services/pdf_service.py`（compress_pdf + COMPRESS_LEVELS）
  - `python-tools/app/routers/pdf.py`（POST /api/pdf/compress）
  - `backend/.../service/FileConvertService.java`（compressPdf 接口方法）
  - `backend/.../service/impl/FileConvertServiceImpl.java`（compressPdf 实现 + callPythonToolsConvert 支持额外表单字段）
  - `backend/.../controller/FileConvertController.java`（POST /pdf-compress）
  - `frontend/src/api/fileConvert.js`（pdfCompress）
  - `frontend/src/views/system/FileConvert.vue`（压缩选项 + 档位 + 压缩率展示）

## 关键决策记录

| 决策点 | 选择 |
|---|---|
| 压缩技术 | PyMuPDF 1.27 三步管线（rewrite_images / subset_fonts / save garbage=4+deflate+clean），无新增依赖、不引 Ghostscript 外部二进制 |
| 压缩档位 | high(200dpi/q80) / medium(150dpi/q65，默认) / low(100dpi/q50)，dpi_threshold=目标+20 |
| 无增益兜底 | 压缩结果 ≥ 原文件时返回原文件内容，不劣化 |
| 加密 PDF | doc.needs_pass 直接拒绝，返回中文错误 |
| 字体子集化 | 独立 try/except，失败不阻断主流程 |
| 权限 | 沿用 system:fileConvert:use，不新增菜单权限 |

## 验证结果

- python-tools HTTP 端到端：7.0MB 测试 PDF（5 页大图）→ high 4.9MB / medium 855KB / low 569KB；页数、文本可选中性、图片完整性均正常；非法档位与非 PDF 均返回 400 中文错误；上传临时目录无残留。
- 后端 `mvn compile` 通过；前端 `npm run build` 通过。
- 遗留：完整环境（DB+COS）下的 UI 全链路待用户本机验证。

## 相关文档
- 提案/设计/任务: C:\data\specs\base\{proposal,design,tasks}.md（specs 仓库统一管理）
- 变更记录: C:\data\specs\base\TEMP.md
