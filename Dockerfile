# ========== 阶段一：前端构建 ==========
FROM node:18-alpine AS frontend-build

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com

COPY frontend/ ./
RUN npm run build

# ========== 阶段二：后端构建 ==========
FROM maven:3.8-openjdk-8 AS backend-build

WORKDIR /app/backend

# 先拷贝 pom.xml 利用 Docker 缓存依赖层
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# ========== 阶段三：运行 ==========
FROM eclipse-temurin:8-jre

# 安装 Nginx
RUN apt-get update && \
    apt-get install -y --no-install-recommends nginx && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 拷贝 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 拷贝前端构建产物
COPY --from=frontend-build /app/frontend/dist /usr/share/nginx/html

# 拷贝后端 jar 包
COPY --from=backend-build /app/backend/target/*.jar /app/app.jar

# 拷贝启动脚本
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# 创建上传目录和日志目录
RUN mkdir -p /app/upload /app/upload/export /app/logs

EXPOSE 80

ENTRYPOINT ["/app/docker-entrypoint.sh"]
