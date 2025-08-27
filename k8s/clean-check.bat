@echo off
echo Checking if cluster is clean...

echo.
echo === Nodes ===
kubectl get nodes

echo.
echo === Pods ===
kubectl get pods --all-namespaces

echo.
echo === Services ===
kubectl get services --all-namespaces

echo.
echo === Docker Containers ===
docker ps -a

echo.
echo === Summary ===
for /f %%i in ('kubectl get pods --no-headers 2^>nul ^| find /c /v ""') do set pod_count=%%i
if "%pod_count%"=="0" (
    echo ✅ No pods found - cluster is clean
) else (
    echo ⚠️ Found %pod_count% pods
)

pause
