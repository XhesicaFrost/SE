Write-Host "清理Kubernetes资源..." -ForegroundColor Yellow

kubectl delete -f frontend.yaml
kubectl delete -f backend.yaml
kubectl delete -f mysql.yaml

Write-Host "清理完成!" -ForegroundColor Green

Read-Host "按任意键继续..."
