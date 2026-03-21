#!/bin/bash

# 启动 Redis
redis-server /etc/redis/redis.conf &

# 启动 Nginx
nginx

# 保持容器运行（Java 由宿主机启动）
tail -f /dev/null