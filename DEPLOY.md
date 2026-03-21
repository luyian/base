# 部署说明

## 环境要求

- Windows 10/11 或 Linux (Ubuntu/CentOS)
- Node.js 18+（用于构建前端）
- Maven 3.8+（用于构建后端）
- MySQL 8.0+
- Redis 6.0+
- Nginx 1.20+

---

## 首次部署

### 1. 环境准备

#### 1.1 安装 MySQL

**Windows（使用 MySQL Installer）:**
1. 下载 MySQL 8.0 安装包
2. 安装时设置端口为 13306（默认3306也可）
3. 设置 root 密码

**Ubuntu:**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

**创建数据库:**
```sql
CREATE DATABASE base_system DEFAULT CHARACTER SET utf8mb4;
```

#### 1.2 安装 Redis

**Windows（使用 WSL 或 Memurai/Redis Windows）:**
```bash
# 使用 WSL2 安装
wsl --install -d Ubuntu
# 然后在 Ubuntu 中安装 Redis
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

**Ubuntu:**
```bash
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

#### 1.3 安装 Nginx

**Windows:**
1. 下载 Nginx for Windows
2. 解压到任意目录

**Ubuntu:**
```bash
sudo apt install nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### 1.4 安装 Node.js

**Windows:**
从 https://nodejs.org 下载 LTS 版本并安装

**Ubuntu:**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs
```

#### 1.5 安装 Maven

**Windows:**
从 https://maven.apache.org 下载并配置环境变量

**Ubuntu:**
```bash
sudo apt install maven
```

---

### 2. 初始化数据库

在 MySQL 中执行初始化 SQL：

```bash
mysql -h 127.0.0.1 -P 13306 -u root -p base_system < backend/src/main/resources/db/schema.sql
mysql -h 127.0.0.1 -P 13306 -u root -p base_system < backend/src/main/resources/db/data.sql
```

---

### 3. 配置后端

修改 `backend/src/main/resources/application-dev.yml` 中的数据库和 Redis 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:13306/base_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: root
    password: 你的MySQL密码

  redis:
    host: localhost
    port: 6379
    password: 你的Redis密码（如果没有则留空）
```

---

### 4. 构建前端

```bash
# 安装依赖
cd frontend
npm install

# 构建生产版本
npm run build
```

构建产物在 `frontend/dist` 目录。

---

### 5. 配置 Nginx

将 `nginx.conf` 中的路径配置为实际路径：

**Windows:**
```nginx
server {
    listen 80;
    server_name _;

    # 前端静态文件（修改为实际构建目录）
    location / {
        root D:/workspace/base/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 反向代理后端 API
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 上传文件访问（修改为实际上传目录）
    location /upload/ {
        alias D:/upload/;
    }
}
```

---

### 6. 启动服务

#### 6.1 启动后端

```bash
cd backend
mvn spring-boot:run
```

或者先打包再运行：
```bash
cd backend
mvn clean package -DskipTests
java -jar target/base-system-1.0.0.jar
```

#### 6.2 启动 Nginx

**Windows:**
```bash
nginx.exe -c D:/workspace/base/nginx.conf
```

**Ubuntu:**
```bash
sudo cp nginx.conf /etc/nginx/sites-available/base-system
sudo ln -s /etc/nginx/sites-available/base-system /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 更新部署

### 1. Git 提交代码

```bash
# 添加所有更改
git add .

# 提交代码（根据实际情况选择）
# 新功能
git commit -m "feat: 添加新功能"

# 修复bug
git commit -m "fix: 修复XXX问题"

# 代码重构
git commit -m "refactor: 重构XXX模块"

# 文档更新
git commit -m "docs: 更新部署文档"

# 推送到远程
git push origin main
```

> 提交规范说明：
> - `feat`: 新功能
> - `fix`: Bug 修复
> - `refactor`: 代码重构
> - `docs`: 文档更新
> - `style`: 代码格式调整
> - `test`: 测试相关
> - `chore`: 构建过程或辅助工具变动

### 2. 重新构建

```bash
# 前端
cd frontend
npm run build

# 后端
cd ../backend
mvn clean package -DskipTests
```

### 3. 重启服务

**后端:**
```bash
# 先停止旧进程，然后重新启动
cd backend
mvn spring-boot:run
```

**Nginx:**
```bash
# Windows
nginx.exe -s reload

# Ubuntu
sudo systemctl reload nginx
```

---

## 开发模式

### 后端热重载

```bash
cd backend
mvn spring-boot:run
```

### 前端热重载

```bash
cd frontend
npm run dev
```

前端开发服务器运行在 http://localhost:3000，后端 API 运行在 http://localhost:8080/api

---

## 日志查看

### 后端日志

```bash
# Windows
type backend\logs\base-system-dev.log

# Ubuntu
tail -f backend/logs/base-system-dev.log
```

### Nginx 日志

```bash
# Windows
type logs\access.log
type logs\error.log

# Ubuntu
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

---

## 进程管理

### 查看端口占用

```bash
# Windows
netstat -ano | findstr "8080"

# Ubuntu
sudo netstat -tlnp | grep 8080
```

### 停止服务

```bash
# 停止后端（Windows）
taskkill /F /PID <进程ID>

# 停止后端（Ubuntu）
sudo kill <进程ID>

# 停止 Nginx
# Windows
nginx.exe -s stop

# Ubuntu
sudo systemctl stop nginx
```

---

## 默认账号

| 用户名 | 密码     | 角色     |
|--------|----------|----------|
| admin  | admin123 | 超级管理员 |
| test   | admin123 | 测试用户  |

---

## 访问地址

| 服务     | 地址                         |
|----------|------------------------------|
| 前端     | http://localhost             |
| API 文档 | http://localhost/doc.html   |

---

## 常见问题

### 1. 端口被占用

修改 `application-dev.yml` 中的端口：
```yaml
server:
  port: 8081
```

### 2. MySQL 连接失败

- 检查 MySQL 服务是否启动
- 验证用户名和密码
- 确认端口号正确

### 3. Redis 连接失败

- 检查 Redis 服务是否启动
- 验证密码配置（如有）

### 4. Nginx 静态页面404

- 确认 `dist` 目录路径正确
- 检查 `try_files` 配置
- 查看 Nginx 错误日志

---

## Docker 部署（可选）

### 环境要求

- Linux 服务器（CentOS / Ubuntu）
- 已安装 Docker
- 已安装 Git
- MySQL 8.0（外部数据库）

---

### 首次部署

#### 1. 上传构建脚本

将 `remote-git-build.sh` 上传到服务器任意目录，转换换行符后赋予执行权限：

```bash
dos2unix remote-git-build.sh
chmod +x remote-git-build.sh
```

#### 2. 初始化数据库

在 MySQL 中创建数据库并执行初始化 SQL：

```sql
CREATE DATABASE base_system DEFAULT CHARACTER SET utf8mb4;
```

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p base_system < backend/src/main/resources/db/schema.sql
mysql -h 127.0.0.1 -P 3306 -u root -p base_system < backend/src/main/resources/db/data.sql
```

#### 3. 构建镜像

```bash
./remote-git-build.sh
```

脚本会自动完成：
- 从 GitHub 拉取代码（master 分支）
- 使用 Node 容器构建前端
- 使用 Maven 容器构建后端
- 打包 Docker 镜像 `base-app`

#### 4. 启动容器

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

### 更新部署

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

### 日志查看

#### 应用日志（Spring Boot）

```bash
# 实时跟踪
docker logs -f base-app-container

# 查看最近 200 行
docker logs --tail 200 base-app-container

# 查看日志文件
docker exec base-app-container tail -f /app/logs/base-system.log
```

#### Nginx 日志

```bash
# 访问日志
docker exec base-app-container tail -f /var/log/nginx/access.log

# 错误日志
docker exec base-app-container tail -f /var/log/nginx/error.log
```

---

### 容器管理

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