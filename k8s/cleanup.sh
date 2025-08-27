#!/bin/bash

echo "清理Kubernetes资源..."

kubectl delete -f frontend.yaml
kubectl delete -f backend.yaml
kubectl delete -f mysql.yaml

echo "清理完成!"
