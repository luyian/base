# 部署文档

本文档详细说明如何在生产环境中部署后台管理系统。

## 目录

- [环境准备](#环境准备)
- [数据库部署](#数据库部署)
- [Redis 部署](#redis-部署)
- [后端部署](#后端部署)
- [前端部署](#前端部署)
- [Nginx 配置](#nginx-配置)
- [系统监控](#系统监控)
- [常见问题](#常见问题)

## 环境准备

### 服务器要求

- **操作系统**：Linux (推荐 CentOS 7+ 或 Ubuntu 18.04+)
- **CPU**：2 核心以上
- **内存**：4GB 以上
- **磁盘**：50GB 以上

### 软件要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 8+ | Java 运行环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存服务 |
| Nginx | 1.18+ | Web 服务器 |

### 安装 JDK

```bash
# 下载 JDK
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz

# 解压
tar -zxvf jdk-17_linux-x64_bin.tar.gz -C /usr/local/

# 配置环境变量
vim /etc/profile

# 添加以下内容
export JAVA_HOME=/usr/local/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# 使配置生效
source /etc/profile

# 验证安装
java -version
```

### 安装 MySQL

```bash
# CentOS 安装
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
rpm -ivh mysql80-community-release-el7-3.noarch.rpm
yum install mysql-server -y

# 启动 MySQL
systemctl start mysqld
systemctl enable mysqld

# 获取临时密码
grep 'temporary password' /var/log/mysqld.log

# 登录并修改密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourPassword123!';
```

### 安装 Redis

```bash
# 下载 Redis
wget https://download.redis.io/releases/redis-6.2.6.tar.gz

# 解压并编译
tar -zxvf redis-6.2.6.tar.gz
cd redis-6.2.6
make
make install

# 配置 Redis
cp redis.conf /etc/redis.conf
vim /etc/redis.conf

# 修改以下配置
bind 0.0.0.0
requirepass YourRedisPassword
daemonize yes

# 启动 Redis
redis-server /etc/redis.conf

# 设置开机自启
echo "redis-server /etc/redis.conf" >> /etc/rc.local
chmod +x /etc/rc.local
```

### 安装 Nginx

```bash
# CentOS 安装
yum install nginx -y

# Ubuntu 安装
apt-get install nginx -y

# 启动 Nginx
systemctl start nginx
systemctl enable nginx
```

## 数据库部署

### 1. 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE base DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户并授权
CREATE USER 'base'@'%' IDENTIFIED BY 'BasePassword123!';
GRANT ALL PRIVILEGES ON base.* TO 'base'@'%';
FLUSH PRIVILEGES;

# 退出
exit;
```

### 2. 导入数据

```bash
# 上传 SQL 文件到服务器
scp backend/src/main/resources/schema.sql root@your-server:/tmp/
scp backend/src/main/resources/data.sql root@your-server:/tmp/

# 导入数据
mysql -u base -p base < /tmp/schema.sql
mysql -u base -p base < /tmp/data.sql
```

### 3. 数据库优化

```sql
-- 修改 MySQL 配置文件 /etc/my.cnf
[mysqld]
# 字符集
character-set-server=utf8mb4
coltion-server=utf8mb4_unicode_ci

# 连接数
max_connections=1000

# 缓冲池大小（根据服务器内存调整）
innodb_buffer_pool_size=1G

# 日志
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2

# 重启 MySQL
systemctl restart mysqld
```

## Redis 部署

### 1. 配置 Redis

```bash
# 编辑配置文件
vim /etc/redis.conf

# 推荐配置
bind 0.0.0.0                    # 监听地址
port 6379                       # 端口
requirepass YourRedisPassword   # 密码
maxmemory 2gb                   # 最大内存
maxmemory-policy allkeys-lru    # 内存淘汰策略
appendonly yes                  # 开启 AOF 持久化
```

### 2. 启动 Redis

```bash
# 启动 Redis
redis-server /etc/redis.conf

# 验证 Redis
redis-cli -a YourRedisPassword ping
```

### 3. Redis 安全配置

```bash
# 禁用危险命令
vim /etc/redis.conf

# 添加以下配置
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command CONFIG ""
rename-command KEYS ""

# 重启 Redis
redis-cli -a YourRedisPassword shutdown
redis-server /etc/redis.conf
```

## 后端部署

### 1. 修改配置文件

创建生产环境配置文件 `application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/base?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: base
    password: BasePassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    password: YourRedisPassword
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms

# JWT 配置
jwt:
  secret: your-production-secret-key-at-least-256-bits-long
  expiration: 86400000  # 24小时

# 文件上传配置
file:
  upload:
    path: /data/upload/
    prefix: /upload
    max-size: 10485760  # 10MB

# 日志配置
logging:
  level:
    root: INFO
    com.base: INFO
  file:
    name: /var/log/base/application.log
    max-size: 100MB
    max-history: 30
```

### 2. 打包后端项目

```bash
# 在本地开发机器上打包
cd backend
mvn clean package -Dmaven.test.skip=true

# 打包完成后，在 target 目录下会生成 JAR 文件
# backend-1.0.0.jar
```

### 3. 上传到服务器

```bash
# 上传 JAR 文件
scp backend/target/backend-1.0.0.jar root@your-server:/opt/base/

# 上传配置文件
scp backend/src/main/resources/application-prod.yml root@your-server:/opt/base/
```

### 4. 创建启动脚本

```bash
# 创建启动脚本
vim /opt/base/start.sh
```

添加以下内容：

```bash
#!/bin/bash

APP_NAME=backend-1.0.0.jar
APP_HOME=/opt/base
LOG_FILE=$APP_HOME/logs/application.log

# 创建日志目录
mkdir -p $APP_HOME/logs

# 检查是否已$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用已在运行，PID: $PID"
    exit 1
fi

# 启动应用
nohup java -jar \
    -Xms512m \
    -Xmx2g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -Dspring.profiles.active=prod \
    $APP_HOME/$APP_NAME \
    > $LOG_FILE 2>&1 &

echo "应用启动成功，PID: $!"
```

创建停止脚本：

```bash
# 创建停止脚本
vim /opt/base/stop.sh
```

添加以下内容：

```bash
#!/bin/bash

APP_NAME=backend-1.0.0.jar

# 查找进程
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 1
fi

# 停止应用
kill -15 $PID

# 等待进程结束
for i in {1..30}; do
    if ! ps -p $PID > /dev/null; then
        echo "应用已停止"
        exit 0
    fi
    sleep 1
done

# 强制停止
kill -9 $PID
echo "应用已强制停止"
```

赋予执行权限：

```bash
chmod +x /opt/base/start.sh
chmod +x /opt/base/stop.sh
```

### 5. 配置系统服务

创建 systemd 服务文件：

```bash
vim /etc/systemd/system/base-backend.service
```

添加以下内容：

```ini
[Unit]
Description=Base Backend Service
After=network.target

[Service]
Type=forking
User=root
WorkingDirectory=/opt/base
ExecStart=/opt/base/start.sh
ExecStop=/opt/base/stop.sh
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用服务：

```bash
# 重新加载 systemd
systemctl daemon-reload

# 启动服务
systemctl start base-backend

# 设置开机自启
systemctl enable base-backend

# 查看状态
systemctl status base-backend

# 查看日志
journalctl -u base-backend -f
```

## 前端部署

### 1. 修改配置文件

创建生产环境配置文件 `.env.production`：

```bash
# API 地址
VITE_API_BASE_URL=http://your-domain.com/api

# 应用标题
VITE_APP_TITLE=后台管理系统
```

### 2. 构建前端项目

```bash
# 在本地开发机器上构建
cd frontend
npm install
npm run build

# 构建完成后，在 dist 目录下会生成静态文件
```

### 3. 上传到服务器

```bash
# 压缩 dist 目录
tar -czf dist.tar.gz dist/

# 上传到服务器
scp dist.tar.gz root@your-server:/tmp/

# 在服务器上解压
ssh root@your-server
cd /usr/share/nginx/html
tar -xzf /tmp/dist.tar.gz
mv dist base
```

## Nginx 配置

### 1. 配置前端

```bash
# 创建 Nginx 配置文件
vim /etc/nginx/conf.d/base.conf
```

添加以下内容：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html/base;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 文件上传路径
    location /upload/ {
        alias /data/upload/;
        expires 30d;
        access_log off;
    }

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1k;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/javascript application/json application/javascript application/x-javascript application/xml;

    # 缓存配置
    location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
        expires 7d;
        access_log off;
    }
}
```

### 2. 配置 HTTPS（可选）

```bash
# 安装 Certbot
yum install certbot python3-certbot-nginx -y

# 获取 SSL 证书
certbot --nginx -d your-domain.com

# 自动续期
echo "0 0 1 * * certbot renew --quiet" >> /var/spool/cron/root
```

HTTPS 配置示例：

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # 其他配置同上...
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

### 3. 重启 Nginx

```bash
# 测试配置
nginx -t

# 重启 Nginx
systemctl restart nginx
```

## 系统监控

### 1. 日志管理

```bash
# 创建日志目录
mkdir -p /var/log/base

# 配置日志轮转
vim /etc/logrotate.d/base
```

添加以下内容：

```
/var/log/base/*.log {
    daily
    rotate 30
    missingok
    notifempty
    compress
    delaycompress
    copy
```

### 2. 监控脚本

创建监控脚本：

```bash
vim /opt/base/monitor.sh
```

添加以下内容：

```bash
#!/bin/bash

APP_NAME=backend-1.0.0.jar
ALERT_EMAIL=admin@example.com

# 检查应用是否运行
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "应用未运行，尝试重启..."
    /opt/base/start.sh

    # 发送告警邮件
    echo "应用异常停止，已自动重启" | mail -s "应用告警" $ALERT_EMAIL
fi

# 检查磁盘空间
DISK_USAGE=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    echo "磁盘空间不足：${DISK_USAGE}%" | mail -s "磁盘告警" $ALERT_EMAIL
fi

# 检查内存使用
MEM_USAGE=$(free | awk 'NR==2 {printf "%.0f", $3/$2*100}')
if [ $MEM_USAGE -gt 90 ]; then
    echo "内存使用过高：${MEM_USAGE}%" | mail -s "内存告警" $ALERT_EMAIL
fi
```

添加到定时任务：

```bash
# 编辑 crontab
crontab -e

# 每 5 分钟检查一次
*/5 * * * * /opt/base/monitor.sh
```

### 3. 性能监控

使用 Prometheus + Grafana 监控（可选）：

```bash
# 在后端项目中添加 actuator 依赖
# pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

# 配置 actuator
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 备份策略

### 1. 数据库备份

```bash
# 创建备份脚本
vim /opt/base/backup-db.sh
```

添加以下内容：

```bash
#!/bin/bash

BACKUP_DIR=/data/backup/mysql
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME=base
DB_USER=base
DB_PASS=BasePassword123!

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u$DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/${DB_NAME}_${DATE}.sql.gz

# 删除 7 天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "数据库备份完成: ${DB_NAME}_${DATE}.sql.gz"
```

添加到定时任务：

```bash
# 每天凌晨 2 点备份
0 2 * * * /opt/base/backup-db.sh
```

### 2. 文件备份

```bash
# 创建文件备份脚本
vim /opt/base/backup-files.sh
```

添加以下内容：

```bash
#!/bin/bash

BACKUP_DIR=/data/backup/files
DATE=$(date +%Y%m%d_%H%M%S)
UPLOAD_DIR=/data/upload

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份上传文件
tar -czf $BACKUP_DIR/upload_${DATE}.tar.gz $UPLOAD_DIR

# 删除 30 天前的备份
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "文件备份完成: upload_${DATE}.tar.gz"
```

## 常见问题

### 1. 应用无法启动

**问题**：执行 start.sh 后应用无法启动

**解决方案**：
```bash
# 查看日志
tail -f /opt/base/logs/application.log

# 检查端口占用
netstat -tunlp | grep 8080

# 检查 Java 版本
java -version
```

### 2. 数据库连接失败

**问题**：应用启动后提示数据库连接失败

**解决方案**：
```bash
# 检查 MySQL 是否运行
systemctl status mysqld

# 测试数据库连接
mysql -u base -p -h localhost base

# 检查防火墙
firewall-cmd --list-ports
firewall-cmd --add-port=3306/tcp --permanent
firewall-cmd --reload
```

### 3. Redis 连接失败

**问题**：应用无法连接 Redis

**解决方案**：
```bash
# 检查 Redis 是否运行
ps -ef | grep redis

# 测试 Redis 连接
redis-cli -a YourRedisPassword ping

# 检查 Redis 配置
vim /etc/redis.conf
# 确保 bind 0.0.0## 4. Nginx 502 错误

**问题**：访问前端页面提示 502 Bad Gateway

**解决方案**：
```bash
# 检查后端是否运行
systemctl status base-backend

# 检查 Nginx 配置
nginx -t

# 查看 Nginx 错误日志
tail -f /var/log/nginx/error.log

# 检查 SELinux
setenforce 0
```

### 5. 文件上传失败

**问题**：上传文件时提示失败

**解决方案**：
```bash
# 检查上传目录权限
ls -la /data/upload

# 修改目录权限
chmod 755 /data/upload
chown -R root:root /data/upload

# 检查磁盘空间
df -h
```

## 性能优化建议

### 1. JVM 参数优化

```bash
# 根据服务器内存调整 JVM 参数
java -jar \
    -Xms2g \
    -Xmx4g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/opt/base/logs/heapdump.hprof \
    backend-1.0.0.jar
```

### 2. MySQL 优化

```sql
-- 添加索引
ALTER TABLE sys_user ADD INDEX idx_username (username);
ALTER TABLE sys_user ADD INDEX idx_dept_id (dept_id);
ALTER TABLE sys_role ADD INDEX idx_role_code (role_code);

-- 定期优化表
OPTIMIZE TABLE sys_user;
OPTIMIZE TABLE sys_role;
OPTIMIZE TABLE sys_permission;
```

### 3. Redis 优化

```bash
# 配置持久化策略
vim /etc/redis.conf

# RDB 持久化
save 900 1
save 300 10
save 60 10000

# AOF 持久化
appendonly yes
appendfsync everysec
```

### 4. Nginx 优化

```nginx
# 增加工作进程数
worker_processes auto;

# 增加连接数
nts {
    worker_connections 10240;
    use epoll;
}

# 开启缓存
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=my_cache:10m max_size=1g inactive=60m;

location /api/ {
    proxy_cache my_cache;
    proxy_cache_valid 200 10m;
    # 其他配置...
}
```

## 安全加固

### 1. 防火墙配置

```bash
# 只开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload

# 限制 SSH 访问
vim /etc/ssh/sshd_config
# 修改 SSH 端口
Port 2222
# 禁止 root 登录
PermitRootLogin no
```

### 2. 定期更新

```bash
# 定期更新系统
yum update -y

# 定期更新依赖
cd backend
mvn versions:display-dependency-updates
```

### 3. 日志审计

```bash
# 启用审计日志
auditctl -w /opt/base -p wa -k base_app
auditctl -w /etc/nginx -p wa -k nginx_config
```

---

**部署完成后，请务必：**

1. 修改所有默认密码
2. 配置防火墙规则
3. 设置定期备份
4. 配置监控告警
5. 进行安全加固

如有问题，请参考常见问题部分或联系技术支持。
