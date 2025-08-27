# Kubernetes 部署指南

## 前置要求
- Docker Desktop 已安装并启用 Kubernetes
- kubectl 命令行工具可用

## 部署步骤

1. 进入 k8s 目录：
   ```bash
   cd k8s
   ```

2. 运行部署脚本：
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```

3. 等待所有服务启动完成

## 访问服务
- 前端服务：http://localhost:30002
- 后端API：http://localhost:30001

## 清理资源
```bash
chmod +x cleanup.sh
./cleanup.sh
```

## 故障排除
- 查看Pod状态：`kubectl get pods`
- 查看服务状态：`kubectl get services`
- 查看Pod日志：`kubectl logs <pod-name>`
