FROM eclipse-temurin:17-jre-alpine

COPY mc-payment-third-party-service-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8100

CMD ["java", "-jar", "app.jar"]
