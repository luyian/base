# 部署说明

## 环境要求

- Linux 服务器（CentOS / Ubuntu）
- 已安装 Docker
- 已安装 Git
- MySQL 8.0（外部数据库，容器内不含 MySQL）

---

## 首次部署

### 1. 上传构建脚本

将 `remote-git-build.sh` 上传到服务器任意目录，转换换行符后赋予执行权限：

```bash
dos2unix remote-git-build.sh
chmod +x remote-git-build.sh
```

### 2. 初始化数据库

在 MySQL 中创建数据库并执行初始化 SQL：

```sql
CREATE DATABASE base_system DEFAULT CHARACTER SET utf8mb4;
```

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p base_system < base/backend/src/main/resources/db/schema.sql
mysql -h 127.0.0.1 -P 3306 -u root -p base_system < base/backend/src/main/resources/db/data.sql
```

### 3. 构建镜像

```bash
./remote-git-build.sh
```

脚本会自动完成：
- 从 GitHub 拉取代码（master 分支）
- 使用 Node 容器构建前端
- 使用 Maven 容器构建后端
- 打包 Docker 镜像 `base-app`

### 4. 启动容器

使用外部 MySQL，通过环境变量注入数据库连接信息：

```bash
docker run -d \
  -p 80:80 \
  -p 6379:6379 \
  --name base-app-container \
  -e DB_HOST=127.0.0.1 \
  -e DB_PORT=3306 \
  -e DB_NAME=base_system \
  -e DB_USER=root \
  -e DB_PASSWORD=your_password \
  base-app
```

> Redis 运行在容器内部，无需外部配置。

---

## 更新部署

代码推送到 GitHub 后，在服务器执行：

```bash
# 停止并删除旧容器
docker rm -f base-app-container

# 重新构建镜像
./remote-git-build.sh

# 启动新容器（同首次部署的 docker run 命令）
docker run -d \
  -p 80:80 \
  -p 6379:6379 \
  --name base-app-container \
  -e DB_HOST=127.0.0.1 \
  -e DB_PORT=3306 \
  -e DB_NAME=base_system \
  -e DB_USER=root \
  -e DB_PASSWORD=your_password \
  base-app
```

跳过 git 拉取（仅重新构建本地代码）：

```bash
./remote-git-build.sh skip
```

---

## 日志查看

### 应用日志（Spring Boot）

```bash
# 实时跟踪
docker logs -f base-app-container

# 查看最近 200 行
docker logs --tail 200 base-app-container

# 查看日志文件
docker exec base-app-container tail -f /app/logs/base-system.log
```

### Nginx 日志

```bash
# 访问日志
docker exec base-app-container tail -f /var/log/nginx/access.log

# 错误日志
docker exec base-app-container tail -f /var/log/nginx/error.log
```

---

## 容器管理

```bash
# 查看运行状态
docker ps

# 停止容器
docker stop base-app-container

# 启动容器
docker start base-app-container

# 重启容器
docker restart base-app-container

# 进入容器
docker exec -it base-app-container bash

# 清理旧镜像（每次重新构建后会产生 <none> 悬空镜像）
docker image prune -f
```

---

## 默认账号

| 用户名 | 密码     | 角色     |
|--------|----------|----------|
| admin  | admin123 | 超级管理员 |
| test   | admin123 | 测试用户  |

---

## 访问地址

| 服务     | 地址                          |
|----------|-------------------------------|
| 前端     | http://服务器IP               |
| API 文档 | http://服务器IP/api/doc.html  |
