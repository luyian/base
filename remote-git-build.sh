#!/bin/bash

# 远程Docker构建脚本
# 通过 Docker 容器完成前后端构建（无需宿主机安装 Node/Maven）
# 构建容器使用宿主机网络下载依赖，最终镜像不需要外网

set -e

REPO_URL="https://github.com/luyian/base.git"
BRANCH="master"
PROJECT_DIR="base"
SKIP_GIT=${1:-false}  # 传入 skip 跳过 git 操作

echo "=== 远程Docker构建脚本 ==="
echo "仓库地址: $REPO_URL"
echo "分支: $BRANCH"

# 拉取代码
if [ "$SKIP_GIT" = "skip" ]; then
    echo "跳过 git 操作，使用已有代码..."
    cd $PROJECT_DIR
elif [ -d "$PROJECT_DIR" ]; then
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
MAVEN_REPO="$HOME/.m2"
mkdir -p "$MAVEN_REPO"

# 生成 Maven settings.xml（阿里云镜像）
cat > "$MAVEN_REPO/settings.xml" << 'EOF'
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
    <mirror>
      <id>aliyun-public</id>
      <mirrorOf>*</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
EOF

docker run --rm \
    --network=host \
    -v "$PROJECT_PATH/backend":/app \
    -v "$MAVEN_REPO":/root/.m2 \
    -w /app \
    maven:3.8-openjdk-8 \
    mvn clean package -DskipTests -B -s /root/.m2/settings.xml

# Docker 镜像打包（不需要网络）
echo "=== Docker 镜像打包 ==="
docker build --network=host -t b

echo "Docker镜像构建完成！"
docker images | grep base-app

echo ""
echo "运行容器:"
echo "docker run -d -p 80:80 -p 6379:6379 --name base-app-container base-app"
