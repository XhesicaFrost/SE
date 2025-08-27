@echo off
echo Cleaning up Kubernetes resources...

kubectl delete -f frontend.yaml
kubectl delete -f backend.yaml
kubectl delete -f mysql.yaml

echo Cleanup completed!

pause
