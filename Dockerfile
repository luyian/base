# ========== 运行阶段 ==========
FROM eclipse-temurin:8-jre

# 安装 Nginx 和 Redis
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        nginx \
        redis-server && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 拷贝 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 从本地 /data/install 路径拷贝文件
# 拷贝前端构建产物
COPY /data/install/frontend/dist /usr/share/nginx/html

# 拷贝后端 jar 包
COPY /data/install/backend/*.jar /app/app.jar

# 拷贝 Redis 配置
COPY redis.conf /etc/redis/redis.conf

# 拷贝启动脚本
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# 创建上传目录、日志目录和Redis目录
RUN mkdir -p /app/upload /app/upload/export /app/logs /var/log/redis /var/lib/redis

# 设置 Redis 配置权限
RUN chown redis:redis /etc/redis/redis.conf

EXPOSE 80 6379

ENTRYPOINT ["/app/docker-entrypoint.sh"]
