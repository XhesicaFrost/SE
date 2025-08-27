@echo off
echo Restarting services with Docker Compose...

echo Step 1: Stopping all services...
docker-compose down --volumes --remove-orphans

echo Step 2: Removing images to force rebuild...
docker rmi takeout-backend:latest takeout-frontend:latest 2>nul
docker image prune -f

echo Step 3: Building and starting services...
docker-compose up --build -d

echo Step 4: Monitoring startup...
timeout /t 5 /nobreak
docker-compose ps

echo Step 5: Showing logs...
docker-compose logs --tail=20

echo Restart completed!
echo Frontend: http://localhost:8080
echo Backend API: http://localhost:12345

pause
