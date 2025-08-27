# Windows 环境下的 Kubernetes 部署指南

## 在Windows中运行Shell脚本的方法

### 方法1：使用批处理文件（推荐）
最简单的方式，双击运行即可：
```cmd
# 部署
k8s\deploy.bat

# 清理
k8s\cleanup.bat
```

### 方法2：使用PowerShell
以管理员身份运行PowerShell：
```powershell
# 设置执行策略（首次使用时需要）
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 部署
.\k8s\deploy.ps1

# 清理
.\k8s\cleanup.ps1
```

### 方法3：使用Git Bash
安装Git for Windows后，右键选择"Git Bash Here"：
```bash
cd k8s
chmod +x deploy.sh
./deploy.sh
```

### 方法4：使用WSL（Windows Subsystem for Linux）
```bash
# 在WSL中运行
cd k8s
chmod +x deploy.sh
./deploy.sh
```

### 方法5：使用Docker Desktop内置的终端
在Docker Desktop中打开终端：
```bash
cd k8s
bash deploy.sh
```

## 推荐使用顺序
1. **deploy.bat** - 最简单，双击运行
2. **deploy.ps1** - PowerShell用户推荐
3. **deploy.sh + Git Bash** - 熟悉Linux命令的用户

## 前置要求
- Docker Desktop 已安装并启用 Kubernetes
- kubectl 命令行工具可用
- 确保Docker Desktop正在运行

## 部署完成后的访问地址
- 前端服务：http://localhost:30002
- 后端API：http://localhost:30001

## 常见问题
1. **权限问题**：右键选择"以管理员身份运行"
2. **执行策略限制**：运行 `Set-ExecutionPolicy RemoteSigned`
3. **Docker未启动**：确保Docker Desktop正在运行
4. **端口冲突**：确保30001和30002端口未被占用
