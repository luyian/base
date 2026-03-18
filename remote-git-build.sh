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

# 拉取代码并进入项目根目录（根目录下应有 frontend/、backend/）
if [ "$SKIP_GIT" = "skip" ]; then
    echo "跳过 git 操作，使用已有代码..."
    if [ -f "frontend/package.json" ]; then
        echo "当前目录即为项目根: $(pwd)"
    elif [ -f "$PROJECT_DIR/frontend/package.json" ]; then
        cd "$PROJECT_DIR"
        echo "已进入项目目录: $(pwd)"
    else
        echo "错误: 找不到 frontend/package.json。"
        echo "请在本脚本所在仓库的根目录执行，或先 cd 到克隆目录（例如 ./$PROJECT_DIR）。"
        echo "当前目录: $(pwd)"
        exit 1
    fi
elif [ -d "$PROJECT_DIR" ]; then
    echo "项目目录已存在，正在更新代码..."
    cd "$PROJECT_DIR"
    git fetch origin $BRANCH
    git reset --hard origin/$BRANCH
else
    echo "克隆项目代码..."
    git clone -b $BRANCH $REPO_URL "$PROJECT_DIR"
    cd "$PROJECT_DIR"
fi

PROJECT_PATH=$(pwd)
if [ ! -f "$PROJECT_PATH/frontend/package.json" ]; then
    echo "错误: $PROJECT_PATH/frontend/package.json 不存在，无法构建前端。"
    exit 1
fi

# 前端构建（使用 Node 容器 + 宿主机网络）
# VITE_LOW_MEM=1：esbuild 压缩、跳过构建期 gzip，避免小内存机 OOM（SIGKILL）
echo "=== 前端构建 ==="
docker run --rm \
    --network=host \
    -e VITE_LOW_MEM=1 \
    -e NODE_OPTIONS="--max-old-space-size=2048" \
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
docker build --network=host -t base-app -f "$PROJECT_PATH/Dockerfile" "$PROJECT_PATH"

echo "Docker镜像构建完成！"
docker images | grep base-app

echo ""
echo "运行容器:"
echo "docker run -d -p 80:80 -p 6379:6379 --name base-app-container base-app"
echo ""
echo "先停掉再删除，然后重新启动"
echo "docker stop base-app-container && docker rm base-app-container && docker run -d --network host --name base-app-container base-app"
echo ""
echo "查看容器日志:"
echo "docker logs -f base-app-container"

