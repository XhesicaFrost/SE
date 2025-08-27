Write-Host "Kubernetes Deployment Status Check" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

Write-Host "`nCluster Info:" -ForegroundColor Yellow
kubectl cluster-info

Write-Host "`nNode Status:" -ForegroundColor Yellow
kubectl get nodes

Write-Host "`nPods Status:" -ForegroundColor Yellow
kubectl get pods -o wide

Write-Host "`nServices Status:" -ForegroundColor Yellow
kubectl get services

Write-Host "`nDeployments Status:" -ForegroundColor Yellow
kubectl get deployments

Write-Host "`nAll Resources Overview:" -ForegroundColor Yellow
kubectl get all

Write-Host "`nPod Logs (if any issues):" -ForegroundColor Yellow

# Check for problematic pods
$problemPods = kubectl get pods --no-headers | Where-Object { $_ -notmatch "Running.*1/1" -and $_ -notmatch "Completed" }

if ($problemPods) {
    Write-Host "Found pods with issues:" -ForegroundColor Red
    foreach ($pod in $problemPods) {
        $podName = ($pod -split '\s+')[0]
        Write-Host "`nLogs for $podName:" -ForegroundColor Cyan
        kubectl logs $podName --tail=20
    }
} else {
    Write-Host "All pods are running normally!" -ForegroundColor Green
}

Write-Host "`nDocker Status:" -ForegroundColor Yellow
Write-Host "Docker containers:" -ForegroundColor Cyan
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"

Write-Host "`nAccess URLs:" -ForegroundColor Cyan
Write-Host "Frontend: http://localhost:30002" -ForegroundColor White
Write-Host "Backend API: http://localhost:30001" -ForegroundColor White

# 检查端口占用情况
Write-Host "`nPort Usage Check:" -ForegroundColor Yellow
try {
    $port30001 = Test-NetConnection -ComputerName localhost -Port 30001 -InformationLevel Quiet -WarningAction SilentlyContinue
    $port30002 = Test-NetConnection -ComputerName localhost -Port 30002 -InformationLevel Quiet -WarningAction SilentlyContinue
    
    if ($port30001) {
        Write-Host "✅ Port 30001 (Backend) is accessible" -ForegroundColor Green
    } else {
        Write-Host "❌ Port 30001 (Backend) is not accessible" -ForegroundColor Red
    }
    
    if ($port30002) {
        Write-Host "✅ Port 30002 (Frontend) is accessible" -ForegroundColor Green
    } else {
        Write-Host "❌ Port 30002 (Frontend) is not accessible" -ForegroundColor Red
    }
} catch {
    Write-Host "Could not check port accessibility" -ForegroundColor Yellow
}

Read-Host "`nPress Enter to exit..."
