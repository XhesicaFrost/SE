Write-Host "Force Cleanup All Takeout Resources" -ForegroundColor Red
Write-Host "====================================" -ForegroundColor Red

$confirmation = Read-Host "This will delete ALL takeout resources. Are you sure? (yes/no)"
if ($confirmation -ne "yes") {
    Write-Host "Operation cancelled" -ForegroundColor Yellow
    exit
}

Write-Host "`n1. Deleting YAML-defined resources..." -ForegroundColor Yellow
kubectl delete -f frontend.yaml --ignore-not-found=true --force --grace-period=0
kubectl delete -f backend.yaml --ignore-not-found=true --force --grace-period=0
kubectl delete -f mysql.yaml --ignore-not-found=true --force --grace-period=0

Write-Host "`n2. Force deleting all takeout-related pods..." -ForegroundColor Yellow
$takeoutPods = kubectl get pods --all-namespaces --no-headers | Where-Object { 
    $_ -match "takeout|mysql|backend|frontend" 
}

if ($takeoutPods) {
    foreach ($podLine in $takeoutPods) {
        $parts = $podLine -split '\s+'
        $namespace = $parts[0]
        $podName = $parts[1]
        Write-Host "Force deleting pod: $namespace/$podName" -ForegroundColor Red
        kubectl delete pod $podName -n $namespace --force --grace-period=0
    }
}

Write-Host "`n3. Deleting services..." -ForegroundColor Yellow
kubectl delete service mysql backend frontend --ignore-not-found=true
kubectl delete service takeout-mysql takeout-backend takeout-frontend --ignore-not-found=true

Write-Host "`n4. Deleting deployments..." -ForegroundColor Yellow
kubectl delete deployment mysql backend frontend --ignore-not-found=true
kubectl delete deployment takeout-mysql takeout-backend takeout-frontend --ignore-not-found=true

Write-Host "`n5. Deleting persistent volume claims..." -ForegroundColor Yellow
kubectl delete pvc --all

Write-Host "`n6. Deleting persistent volumes..." -ForegroundColor Yellow
$pvs = kubectl get pv --no-headers | Where-Object { $_ -match "mysql|takeout" }
if ($pvs) {
    foreach ($pv in $pvs) {
        $pvName = ($pv -split '\s+')[0]
        Write-Host "Deleting PV: $pvName" -ForegroundColor Red
        kubectl delete pv $pvName --force
    }
}

Write-Host "`n7. Cleaning up Docker containers..." -ForegroundColor Yellow
docker stop takeout_mysql takeout_backend takeout_frontend 2>$null
docker rm takeout_mysql takeout_backend takeout_frontend 2>$null

# 清理所有带 takeout 标签的容器
$takeoutContainers = docker ps -a --filter "name=takeout" --format "{{.Names}}"
if ($takeoutContainers) {
    Write-Host "Found takeout containers, removing..." -ForegroundColor Red
    foreach ($container in $takeoutContainers) {
        docker stop $container 2>$null
        docker rm $container 2>$null
    }
}

Write-Host "`n8. Cleaning up Docker images..." -ForegroundColor Yellow
docker rmi takeout-backend:latest takeout-frontend:latest 2>$null

Write-Host "`n9. Final verification..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

$remainingPods = kubectl get pods --no-headers 2>$null
if ($remainingPods) {
    Write-Host "⚠️ Some pods still exist:" -ForegroundColor Yellow
    $remainingPods | ForEach-Object { Write-Host "  $_" -ForegroundColor Yellow }
} else {
    Write-Host "✅ No pods found" -ForegroundColor Green
}

$remainingServices = kubectl get services --no-headers | Where-Object { 
    $_ -notmatch "kubernetes.*ClusterIP.*10\.96\.0\.1.*443" 
}
if ($remainingServices) {
    Write-Host "⚠️ Some services still exist:" -ForegroundColor Yellow
    $remainingServices | ForEach-Object { Write-Host "  $_" -ForegroundColor Yellow }
} else {
    Write-Host "✅ Only default kubernetes service exists" -ForegroundColor Green
}

Write-Host "`nForce cleanup completed!" -ForegroundColor Green
Read-Host "Press Enter to exit..."
