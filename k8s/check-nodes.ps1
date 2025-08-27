Write-Host "Kubernetes Cluster and Node Status Check" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

# 检查节点状态
Write-Host "`n1. Node Status:" -ForegroundColor Yellow
kubectl get nodes -o wide

# 检查所有命名空间的 Pods
Write-Host "`n2. All Pods (All Namespaces):" -ForegroundColor Yellow
kubectl get pods --all-namespaces -o wide

# 检查当前命名空间的所有资源
Write-Host "`n3. All Resources in Current Namespace:" -ForegroundColor Yellow
kubectl get all

# 检查持久卷
Write-Host "`n4. Persistent Volumes:" -ForegroundColor Yellow
kubectl get pv

# 检查持久卷声明
Write-Host "`n5. Persistent Volume Claims:" -ForegroundColor Yellow
kubectl get pvc

# 检查服务
Write-Host "`n6. Services:" -ForegroundColor Yellow
kubectl get services -o wide

# 检查部署
Write-Host "`n7. Deployments:" -ForegroundColor Yellow
kubectl get deployments -o wide

# 检查副本集
Write-Host "`n8. ReplicaSets:" -ForegroundColor Yellow
kubectl get rs

# 检查配置映射
Write-Host "`n9. ConfigMaps:" -ForegroundColor Yellow
kubectl get configmaps

# 检查密钥
Write-Host "`n10. Secrets:" -ForegroundColor Yellow
kubectl get secrets

# 检查 Takeout 相关资源
Write-Host "`n11. Takeout Related Resources:" -ForegroundColor Yellow
Write-Host "Searching for takeout, mysql, backend, frontend resources..." -ForegroundColor Cyan

$takeoutPods = kubectl get pods --all-namespaces --no-headers | Where-Object { 
    $_ -match "takeout|mysql|backend|frontend" 
}

if ($takeoutPods) {
    Write-Host "Found takeout-related pods:" -ForegroundColor Red
    $takeoutPods | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
} else {
    Write-Host "No takeout-related pods found" -ForegroundColor Green
}

$takeoutServices = kubectl get services --all-namespaces --no-headers | Where-Object { 
    $_ -match "takeout|mysql|backend|frontend" 
}

if ($takeoutServices) {
    Write-Host "Found takeout-related services:" -ForegroundColor Red
    $takeoutServices | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
} else {
    Write-Host "No takeout-related services found" -ForegroundColor Green
}

# 检查 Docker 容器
Write-Host "`n12. Docker Containers:" -ForegroundColor Yellow
$dockerContainers = docker ps -a --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
if ($dockerContainers) {
    Write-Host $dockerContainers
} else {
    Write-Host "No Docker containers found" -ForegroundColor Green
}

# 检查 Docker 镜像
Write-Host "`n13. Docker Images:" -ForegroundColor Yellow
$takeoutImages = docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.ID}}\t{{.Size}}" | Where-Object { 
    $_ -match "takeout|mysql" 
}

if ($takeoutImages) {
    Write-Host "Found takeout-related images:" -ForegroundColor Cyan
    $takeoutImages | ForEach-Object { Write-Host "  $_" -ForegroundColor Cyan }
} else {
    Write-Host "No takeout-related images found" -ForegroundColor Green
}

# 资源使用情况
Write-Host "`n14. Resource Usage:" -ForegroundColor Yellow
try {
    kubectl top nodes 2>$null
    kubectl top pods 2>$null
} catch {
    Write-Host "Metrics server not available" -ForegroundColor Yellow
}

# 总结
Write-Host "`n15. Summary:" -ForegroundColor Green
$allPods = kubectl get pods --no-headers 2>$null
$runningPods = $allPods | Where-Object { $_ -match "Running" }

if ($allPods) {
    Write-Host "Total pods: $($allPods.Count)" -ForegroundColor White
    Write-Host "Running pods: $($runningPods.Count)" -ForegroundColor White
    
    if ($runningPods.Count -eq 0) {
        Write-Host "✅ All pods are stopped/cleaned" -ForegroundColor Green
    } else {
        Write-Host "⚠️  There are still running pods" -ForegroundColor Yellow
    }
} else {
    Write-Host "✅ No pods found - cluster is clean" -ForegroundColor Green
}

Read-Host "`nPress Enter to exit..."
