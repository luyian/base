#!/bin/bash

# 远程Docker构建脚本
# 通过 Docker 容器完成前后端构建（无需宿主机安装 Node/Maven）
# 构建容器使用宿主机网络下载依赖，最终镜像不需要外网

set -e

REPO_URL="https://github.com/luyian/base.git"
BRANCH="master"
PROJECT_DIR="base"

echo "=== 远程Docker构建脚本 ==="
echo "仓库地址: $REPO_URL"
echo "分支: $BRANCH"

# 拉取代码
if [ -d "$PROJECT_DIR" ]; then
    echo "项目目录已存在，正在更新代码..."
    cd $PROJECT_DIR
    git fetch origin $BRANCH
    git reset --hard origin/$BRANCH
else
    echo "克隆项目代码..."
    git clone -b $BRANCH $REPO_URL $PROJECT_DIR
    cd $PROJECT_DIR
fi

PROJECT_PATH=$(pwd)

# 前端构建（使用 Node 容器 + 宿主机网络）
echo "=== 前端构建 ==="
docker run --rm \
    --network=host \
    -v "$PROJECT_PATH/frontend":/app \
    -w /app \
    node:20-alpine \
    sh -c "npm install --registry=https://registry.npmmirror.com && npm run build"

# 后端构建（使用 Maven 容器 + 宿主机网络 + 缓存本地仓库）
echo "=== 后端构建 ==="
mkdir -p "$PROJECT_PATH/.m2"
docker run --rm \
    --network=host \
    -v "$PROJECT_PATH/backend":/app \
    -v "$PROJECT_PATH/.m2":/root/.m2 \
    -w /app \
    maven:3.8-openjdk-8 \
    mvn clean package -DskipTests -B

# Docker 镜像打包（不需要网络）
echo "=== Docker 镜像打包 ==="
docker build -t base-app .

echo "Docker镜像构建完成！"
docker images | grep base-app

echo ""
echo "运行容器:"
echo "docker run -d -p 80:80 -p 6379:6379 --name base-app-container base-app"
