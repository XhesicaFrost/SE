Write-Host "Starting complete restart process..." -ForegroundColor Green

Write-Host "`nStep 1: Cleaning up existing Kubernetes resources..." -ForegroundColor Yellow
kubectl delete -f frontend.yaml --ignore-not-found=true
kubectl delete -f backend.yaml --ignore-not-found=true
kubectl delete -f mysql.yaml --ignore-not-found=true

Write-Host "Waiting for resources to be fully terminated..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "`nStep 2: Checking for remaining pods..." -ForegroundColor Yellow
$remainingPods = kubectl get pods --no-headers 2>$null
if ($remainingPods) {
    Write-Host "Found remaining pods, force deleting..." -ForegroundColor Red
    foreach ($pod in $remainingPods) {
        $podName = ($pod -split '\s+')[0]
        Write-Host "Force deleting pod: $podName" -ForegroundColor Red
        kubectl delete pod $podName --force --grace-period=0
    }
    Start-Sleep -Seconds 5
}

Write-Host "`nStep 3: Cleanup Docker containers..." -ForegroundColor Yellow
docker stop takeout_mysql takeout_backend takeout_frontend 2>$null
docker rm takeout_mysql takeout_backend takeout_frontend 2>$null

Write-Host "`nStep 4: Remove old images to force rebuild..." -ForegroundColor Yellow
docker rmi takeout-backend:latest takeout-frontend:latest 2>$null

Write-Host "`nStep 5: Building new Docker images..." -ForegroundColor Green

# Build backend
Set-Location -Path "..\back_end"
Write-Host "Building backend image..." -ForegroundColor Cyan
docker build -t takeout-backend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Build frontend
Set-Location -Path "..\front_end"
Write-Host "Building frontend image..." -ForegroundColor Cyan
docker build -t takeout-frontend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Frontend build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Set-Location -Path "..\k8s"

Write-Host "`nStep 6: Deploying fresh services..." -ForegroundColor Green

# Deploy MySQL
Write-Host "Deploying MySQL..." -ForegroundColor Cyan
kubectl apply -f mysql.yaml
Write-Host "Waiting for MySQL to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Deploy Backend
Write-Host "Deploying Backend..." -ForegroundColor Cyan
kubectl apply -f backend.yaml
Write-Host "Waiting for Backend to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Deploy Frontend
Write-Host "Deploying Frontend..." -ForegroundColor Cyan
kubectl apply -f frontend.yaml

Write-Host "`nStep 7: Monitoring startup..." -ForegroundColor Green
$timeout = 120
$elapsed = 0

do {
    Start-Sleep -Seconds 5
    $elapsed += 5
    
    Write-Host "`nCurrent status (${elapsed}s elapsed):" -ForegroundColor Cyan
    kubectl get pods
    
    $runningPods = kubectl get pods --no-headers | Where-Object { $_ -match "Running.*1/1" }
    $totalPods = kubectl get pods --no-headers | Measure-Object | Select-Object -ExpandProperty Count
    
    if ($runningPods.Count -eq $totalPods) {
        Write-Host "`nAll pods are running successfully!" -ForegroundColor Green
        break
    }
    
    Write-Host "Waiting for $($totalPods - $runningPods.Count) more pod(s)..." -ForegroundColor Yellow
    
} while ($elapsed -lt $timeout)

Write-Host "`nFinal Status:" -ForegroundColor Green
kubectl get pods
kubectl get services

Write-Host "`nRestart completed!" -ForegroundColor Green
Write-Host "Frontend: http://localhost:30002" -ForegroundColor Cyan
Write-Host "Backend API: http://localhost:30001" -ForegroundColor Cyan

if ($elapsed -ge $timeout) {
    Write-Host "`nWarning: Some pods may still be starting up." -ForegroundColor Yellow
}

Read-Host "`nPress Enter to exit"
