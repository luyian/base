# 部署说明

## 环境要求

- Linux (Ubuntu/CentOS)
- Node.js 18+（用于构建前端）
- Maven 3.8+（用于构建后端）
- MySQL 8.0+
- Redis 6.0+
- Nginx 1.20+

## 服务器信息

| 服务 | 地址 | 端口 |
|------|------|------|
| 后端 | 119.45.176.101 | 8080 |
| 前端 | 119.45.176.101 | 80 |
| MySQL | 127.0.0.1 | 13306 |
| Redis | 127.0.0.1 | 6379 |

---

## 首次部署

### 1. 环境准备

#### 1.1 安装 MySQL

```bash
# Ubuntu
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

```bash
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

#### 1.3 安装 Nginx

```bash
sudo apt install nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### 1.4 安装 Node.js

```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs
```

#### 1.5 安装 Maven

```bash
sudo apt install maven
```

---

### 2. 初始化数据库

在 MySQL 中执行初始化 SQL：

```bash
mysql -h 127.0.0.1 -P 13306 -u root -p base_system < backend/src/main/resources/db/tables.sql
mysql -h 127.0.0.1 -P 13306 -u root -p base_system < backend/src/main/resources/db/data.sql
```

---

### 3. 配置后端

修改 `backend/src/main/resources/application-prod.yml` 中的数据库和 Redis 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:13306/base_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: root
    password: NiCaiCai@@

  redis:
    host: localhost
    port: 6379
    password: your_redis_password
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

配置文件路径：`/etc/nginx/sites-available/base-system`

```nginx
server {
    listen 80;
    server_name _;

    # 前端静态文件
    location / {
        root /home/workspace/base/frontend/dist;
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

    # 上传文件访问
    location /upload/ {
        alias /data/upload/;
    }

    # 文件下载
    location /api/system/export/task/ {
        alias /data/export/;
    }
}
```

---

### 6. 启动服务

#### 6.1 启动后端

```bash
cd /home/workspace/base/backend
mvn clean package -DskipTests
java -jar target/base-system-1.0.0.jar --spring.profiles.active=prod
```

#### 6.2 重启 Nginx

```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

## 更新部署

### 1. 拉取最新代码

```bash
cd /home/workspace/base
git pull origin dev_ai_auto
```

### 2. 重新构建

```bash
# 前端
cd /home/workspace/base/frontend
npm run build

# 后端
cd /home/workspace/base/backend
mvn clean package -DskipTests
```

### 3. 重启服务

**查找并停止旧进程：**
```bash
# 查找 Java 进程
ps aux | grep base-system

# 停止旧进程
kill -9 <进程ID>
```

**启动新进程：**
```bash
cd /home/workspace/base/backend
nohup java -jar target/base-system-1.0.0.jar --spring.profiles.active=prod > logs/base-system-prod.log 2>&1 &
```

**重载 Nginx：**
```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

## 开发模式

### 后端热重载

```bash
cd /home/workspace/base/backend
mvn spring-boot:run
```

### 前端热重载

```bash
cd /home/workspace/base/frontend
npm run dev
```

前端开发服务器运行在 http://localhost:3000，后端 API 运行在 http://localhost:8080/api

---

## 日志查看

### 后端日志

```bash
tail -f /home/workspace/base/backend/logs/base-system-prod.log
```

### Nginx 日志

```bash
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

---

## 进程管理

### 查看端口占用

```bash
sudo netstat -tlnp | grep 8080
sudo netstat -tlnp | grep 80
```

### 停止服务

```bash
# 停止后端
ps aux | grep base-system
kill -9 <进程ID>

# 停止 Nginx
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
| 前端     | http://119.45.176.101       |
| API 文档 | http://119.45.176.101/doc.html |

---

## 常见问题

### 1. 端口被占用

修改 `application-prod.yml` 中的端口：
```yaml
server:
  port: 8081
```

### 2. MySQL 连接失败

- 检查 MySQL 服务是否启动
- 验证用户名和密码
- 确认端口号正确（13306）

### 3. Redis 连接失败

- 检查 Redis 服务是否启动
- 验证密码配置

### 4. Nginx 静态页面404

- 确认 `dist` 目录路径正确
- 检查 `try_files` 配置
- 查看 Nginx 错误日志

### 5. 前端请求后端失败

- 检查后端服务是否启动
- 检查 Nginx 代理配置
- 检查防火墙是否开放端口

---

## 目录结构

```
/home/workspace/base/
├── backend/
│   ├── src/main/java/com/base/
│   │   ├── ai/              # AI 大模型模块
│   │   ├── common/          # 公共模块
│   │   ├── message/         # 消息推送模块
│   │   ├── stock/           # 股票分析模块
│   │   └── system/          # 系统管理模块
│   ├── src/main/resources/
│   │   ├── db/              # 数据库脚本
│   │   │   ├── tables.sql   # 表结构
│   │   │   └── data.sql     # 初始数据
│   │   └── application*.yml # 配置文件
│   ├── target/
│   │   └── base-system-1.0.0.jar
│   └── logs/
│
├── frontend/
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── views/           # 页面组件
│   │   └── ...
│   └── dist/                # 构建产物
│
└── docs/                    # 文档
```

---

**最后更新：** 2026-03-27