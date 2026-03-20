# 文件上传下载接口开发计划

## 目标
在 base 项目开发通用文件上传下载接口，菜单增加文件上传下载日志，文件系统使用 FastDFS

## 技术栈
- 后端: Spring Boot 2.7.18 + MyBatis Plus
- 前端: Vue 3 + Element Plus
- 文件系统: FastDFS
- 数据库: MySQL

## 阶段

### Phase 1: 安装 FastDFS
- [x] 安装 FastDFS 和 nginx 模块
- [x] 配置 FastDFS 存储
- [x] 验证 FastDFS 服务

### Phase 2: 后端开发
- [x] 添加 FastDFS Java 客户端依赖
- [x] 创建文件上传下载实体 (SysFile)
- [x] 创建文件日志实体 (SysFileLog)
- [x] 开发 FileController (上传/下载接口)
- [x] 开发 FileService
- [x] 添加上传下载日志记录

### Phase 3: 前端开发
- [x] 添加文件上传下载菜单数据
- [x] 创建文件管理页面
- [x] 创建文件日志页面

### Phase 4: 测试
- [x] 测试文件上传
- [x] 测试文件下载
- [x] 测试日志记录
- [x] 提交代码

## 决策记录
| 日期 | 决策 | 原因 |
|------|------|------|
| 2026-03-20 | 使用 FastDFS 5.11 | 稳定版本 |