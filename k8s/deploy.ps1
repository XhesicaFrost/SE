Write-Host "Building Docker images..." -ForegroundColor Green

# Build backend image
Set-Location -Path "..\back_end"
docker build -t takeout-backend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend image build failed" -ForegroundColor Red
    exit 1
}

# Build frontend image
Set-Location -Path "..\front_end"
docker build -t takeout-frontend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Frontend image build failed" -ForegroundColor Red
    exit 1
}

Set-Location -Path "..\k8s"

Write-Host "Deploying to Kubernetes..." -ForegroundColor Green

# Deploy MySQL
kubectl apply -f mysql.yaml
Write-Host "Waiting for MySQL to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Deploy backend
kubectl apply -f backend.yaml
Write-Host "Waiting for backend to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Deploy frontend
kubectl apply -f frontend.yaml

Write-Host "Deployment completed!" -ForegroundColor Green
Write-Host "Frontend URL: http://localhost:30002" -ForegroundColor Cyan
Write-Host "Backend API URL: http://localhost:30001" -ForegroundColor Cyan

Write-Host "Checking service status:" -ForegroundColor Yellow
kubectl get pods
kubectl get services

Write-Host "`nWaiting for all containers to be ready..." -ForegroundColor Yellow
Write-Host "This may take 1-2 minutes for frontend container..." -ForegroundColor Yellow

# Wait for all pods to be ready
$timeout = 120
$elapsed = 0
do {
    Start-Sleep -Seconds 5
    $elapsed += 5
    $readyPods = kubectl get pods --no-headers | Where-Object { $_ -match "Running.*1/1" }
    $allPods = kubectl get pods --no-headers
    Write-Host "Ready pods: $($readyPods.Count) / Total pods: $($allPods.Count)" -ForegroundColor Cyan
    
    if ($readyPods.Count -eq $allPods.Count) {
        Write-Host "All pods are ready!" -ForegroundColor Green
        break
    }
} while ($elapsed -lt $timeout)

if ($elapsed -ge $timeout) {
    Write-Host "Timeout waiting for pods to be ready. Please check manually." -ForegroundColor Red
}

Read-Host "Press Enter to continue..."
