# Base 系统开发部署流程

## 环境说明
- **Nginx**：宿主机自带（端口 80）
- **Redis**：宿主机自带（端口 6379）
- **MySQL**：119.45.176.101:13306
- **Java**：宿主机直接运行

---

## 部署流程

### 1. 前端编译
```bash
cd /home/workspace/base/frontend
npm run build
```

### 2. 复制到 Nginx 目录
```bash
sudo rm -rf /usr/share/nginx/html/*
sudo cp -r /home/workspace/base/frontend/dist/* /usr/share/nginx/html/
sudo chown -R www-data:www-data /usr/share/nginx/html/
```

### 3. 启动后端
```bash
screen -dmS backend bash -c 'cd /home/workspace/base/backend && exec java \
  -Djava.security.egd=file:/dev/./urandom \
  -Duser.timezone=GMT+8 \
  -Dspring.profiles.active=docker \
  -DDB_HOST=119.45.176.101 \
  -DDB_PORT=13306 \
  -DDB_NAME=base_system \
  -DDB_USER=root \
  -DDB_PASSWORD="NiCaiCai@@" \
  -DREDIS_HOST=127.0.0.1 \
  -DREDIS_PORT=6379 \
  -jar target/base-system-1.0.0.jar'
```

---

## 快速命令

```bash
# 一键部署
cd /home/workspace/base/frontend && npm run build && \
sudo rm -rf /usr/share/nginx/html/* && \
sudo cp -r /home/workspace/base/frontend/dist/* /usr/share/nginx/html/ && \
sudo chown -R www-data:www-data /usr/share/nginx/html/
```

---

## 重启后端
```bash
screen -S backend -X quit
screen -dmS backend /home/workspace/base/start-backend.sh
```

---

## 验证
```bash
# 前端
curl http://localhost/

# 后端
curl http://localhost:8080/api/auth/login

# Redis
redis-cli ping
```

---

## 端口

| 服务 | 端口 |
|-----|------|
| Nginx | 80 |
| Java | 8080 |
| Redis | 6379 |