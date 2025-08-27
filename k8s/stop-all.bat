@echo off
echo Stopping all takeout services...

echo Step 1: Deleting Kubernetes resources...
kubectl delete -f frontend.yaml --ignore-not-found=true
kubectl delete -f backend.yaml --ignore-not-found=true
kubectl delete -f mysql.yaml --ignore-not-found=true

echo Step 2: Force delete any remaining pods...
FOR /F "tokens=1" %%i IN ('kubectl get pods --no-headers 2^>nul') DO (
    echo Force deleting pod %%i
    kubectl delete pod %%i --force --grace-period=0
)

echo Step 3: Stop and remove Docker containers...
docker stop takeout_mysql takeout_backend takeout_frontend 2>nul
docker rm takeout_mysql takeout_backend takeout_frontend 2>nul

echo Step 4: Check final status...
kubectl get pods
docker ps -a | findstr takeout

echo All services stopped!
pause
