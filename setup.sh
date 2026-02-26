#!/bin/bash

# CentOS 7 一键安装脚本：Docker、MySQL 8.0（Docker）、Git
# 用法：bash setup.sh

set -e

echo "=== CentOS 7 环境初始化脚本 ==="

# ========== 安装 Git ==========
echo "=== 安装 Git ==="
if command -v git &>/dev/null; then
    echo "Git 已安装：$(git --version)"
else
    yum install -y git
    echo "Git 安装完成：$(git --version)"
fi

# ========== 安装 Docker ==========
echo "=== 安装 Docker ==="
if command -v docker &>/dev/null; then
    echo "Docker 已安装：$(docker --version)"
else
    # 安装依赖
    yum install -y yum-utils device-mapper-persistent-data lvm2

    # 添加阿里云 Docker 源
    yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

    # 安装 Docker
    yum install -y docker-ce docker-ce-cli containerd.io

    # 配置镜像加速
    mkdir -p /etc/docker
    cat > /etc/docker/daemon.json << 'EOF'
{
    "registry-mirrors": [
        "https://mirror.ccs.tencentyun.com",
        "https://docker.mirrors.ustc.edu.cn"
    ]
}
EOF

    # 启动并设置开机自启
    systemctl start docker
    systemctl enable docker
    echo "Docker 安装完成：$(docker --version)"
fi

# ========== 安装 MySQL 8.0（Docker） ==========
echo "=== 安装 MySQL 8.0 ==="
if docker ps -a --format '{{.Names}}' | grep -q '^mysql8$'; then
    echo "MySQL 容器已存在"
    docker start mysql8 2>/dev/null || true
else
    # 创建数据目录
    mkdir -p /data/mysql/data /data/mysql/conf

    # MySQL 配置
    cat > /data/mysql/conf/my.cnf << 'EOF'
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-time-zone='+08:00'
max_connections=200
innodb_buffer_pool_size=256M

[client]
default-character-set=utf8mb4
EOF

    # 拉取并启动 MySQL
    docker run -d \
        --name mysql8 \
        -p 13306:3306 \
        -m 1g \
        -e MYSQL_ROOT_PASSWORD=Nicacai@@@ \
        -v /data/mysql/data:/var/lib/mysql \
        -v /data/mysql/conf:/etc/mysql/conf.d \
        --restart=always \
        mysql:8.0

    # 等待 MySQL 启动
    echo "等待 MySQL 启动..."
    for i in $(seq 1 30); do
        if docker exec mysql8 mysql -uroot -p'Nicacai@@@' -e "SELECT 1" &>/dev/null; then
            echo "MySQL 启动成功"
            break
        fi
        sleep 2
    done

    # 授权远程访问
    docker exec mysql8 mysql -uroot -p'Nicacai@@@' -e "
        ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'Nicacai@@@';
        FLUSH PRIVILEGES;
    "
    echo "MySQL 安装完成，端口：13306"
fi

# ========== 完成 ==========
echo ""
echo "=== 安装完成 ==="
echo "Git:    $(git --version)"
echo "Docker: $(docker --version)"
echo "MySQL:  端口 13306，密码 Nicacai@@@"
echo ""
echo "MySQL 连接方式："
echo "  本地：docker exec -it mysql8 mysql -uroot -p"
echo "  远程：mysql -h <公网IP> -P 13306 -uroot -p"
