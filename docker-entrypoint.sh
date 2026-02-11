#!/bin/bash

# 启动 Nginx
nginx

# 启动 Java 应用（前台运行，保持容器存活）
exec java \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=docker \
    -DDB_HOST=${DB_HOST:-localhost} \
    -DDB_PORT=${DB_PORT:-3306} \
    -DDB_NAME=${DB_NAME:-base_system} \
    -DDB_USER=${DB_USER:-root} \
    -DDB_PASSWORD=${DB_PASSWORD:-123456} \
    -DREDIS_HOST=${REDIS_HOST:-localhost} \
    -DREDIS_PORT=${REDIS_PORT:-6379} \
    -DREDIS_PASSWORD=${REDIS_PASSWORD:-} \
    ${JAVA_OPTS} \
    -jar /app/app.jar
