@echo off
echo Building Docker images...

REM Build backend image
cd ..\back_end
docker build -t takeout-backend:latest .

REM Build frontend image
cd ..\front_end
docker build -t takeout-frontend:latest .

cd ..\k8s

echo Deploying to Kubernetes...

REM Deploy MySQL
kubectl apply -f mysql.yaml

REM Wait for MySQL to start
echo Waiting for MySQL to start...
timeout /t 30 /nobreak

REM Deploy backend
kubectl apply -f backend.yaml

REM Wait for backend to start
echo Waiting for backend to start...
timeout /t 20 /nobreak

REM Deploy frontend
kubectl apply -f frontend.yaml

echo Deployment completed!
echo Frontend URL: http://localhost:30002
echo Backend API URL: http://localhost:30001

echo Checking service status:
kubectl get pods
kubectl get services

echo.
echo Waiting for frontend container to be ready...
echo Please wait 30-60 seconds for frontend to fully start.

pause
