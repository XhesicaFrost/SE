@echo off
echo Starting complete restart process...

echo Step 1: Cleaning up existing Kubernetes resources...
kubectl delete -f frontend.yaml --ignore-not-found=true
kubectl delete -f backend.yaml --ignore-not-found=true  
kubectl delete -f mysql.yaml --ignore-not-found=true

echo Waiting for resources to be fully terminated...
timeout /t 10 /nobreak

echo Step 2: Checking for remaining pods...
kubectl get pods

echo Step 3: Force cleanup if needed...
FOR /F "tokens=1" %%i IN ('kubectl get pods --no-headers 2^>nul') DO (
    echo Force deleting pod %%i
    kubectl delete pod %%i --force --grace-period=0
)

echo Step 4: Cleanup Docker containers and images...
docker stop takeout_mysql takeout_backend takeout_frontend 2>nul
docker rm takeout_mysql takeout_backend takeout_frontend 2>nul

echo Step 5: Remove old images to force rebuild...
docker rmi takeout-backend:latest takeout-frontend:latest 2>nul

echo Step 6: Building new Docker images...
cd ..\back_end
docker build -t takeout-backend:latest .
if errorlevel 1 (
    echo Backend build failed!
    pause
    exit /b 1
)

cd ..\front_end  
docker build -t takeout-frontend:latest .
if errorlevel 1 (
    echo Frontend build failed!
    pause
    exit /b 1
)

cd ..\k8s

echo Step 7: Deploying fresh services...
kubectl apply -f mysql.yaml
echo Waiting for MySQL...
timeout /t 30 /nobreak

kubectl apply -f backend.yaml
echo Waiting for Backend...
timeout /t 20 /nobreak

kubectl apply -f frontend.yaml

echo Step 8: Final status check...
kubectl get pods
kubectl get services

echo Restart completed!
echo Frontend: http://localhost:30002
echo Backend API: http://localhost:30001

pause
