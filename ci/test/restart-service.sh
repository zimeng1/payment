cd /opt/app/mc-payment-service && sudo docker build -t mc-payment-service . --no-cache
cd /opt/app/mc-payment-service && docker-compose -f docker-compose.yaml up -d mc-payment-service
docker image prune -f