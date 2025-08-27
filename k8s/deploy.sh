#!/bin/bash

# Windows兼容性：设置换行符
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    echo "检测到Windows环境"
fi

echo "开始构建Docker镜像..."

# 构建后端镜像
cd ../back_end || exit 1
docker build -t takeout-backend:latest .
if [ $? -ne 0 ]; then
    echo "后端镜像构建失败"
    exit 1
fi

# 构建前端镜像
cd ../front_end || exit 1
docker build -t takeout-frontend:latest .
if [ $? -ne 0 ]; then
    echo "前端镜像构建失败"
    exit 1
fi

cd ../k8s || exit 1

echo "开始部署到Kubernetes..."

# 部署MySQL
kubectl apply -f mysql.yaml
if [ $? -ne 0 ]; then
    echo "MySQL部署失败"
    exit 1
fi

# 等待MySQL启动
echo "等待MySQL启动..."
sleep 30

# 部署后端
kubectl apply -f backend.yaml
if [ $? -ne 0 ]; then
    echo "后端部署失败"
    exit 1
fi

# 等待后端启动
echo "等待后端启动..."
sleep 20

# 部署前端
kubectl apply -f frontend.yaml
if [ $? -ne 0 ]; then
    echo "前端部署失败"
    exit 1
fi

echo "部署完成!"
echo "前端访问地址: http://localhost:30002"
echo "后端API地址: http://localhost:30001"

echo "检查服务状态:"
kubectl get pods
kubectl get services
