#!/bin/bash
trap '' HUP
cd /home/workspace/base/backend
exec java \
  -Djava.security.egd=file:/dev/./urandom \
  -Duser.timezone=GMT+8 \
  -Dspring.profiles.active=docker \
  -DDB_HOST=119.45.176.101 \
  -DDB_PORT=13306 \
  -DDB_NAME=base_system \
  -DDB_USER=root \
  -DDB_PASSWORD='NiCaiCai@@' \
  -DREDIS_HOST=127.0.0.1 \
  -DREDIS_PORT=6379 \
  -jar target/base-system-1.0.0.jar