FROM eclipse-temurin:17-jre-alpine

COPY mc-payment-service-0.0.1-SNAPSHOT.jar /app/app.jar

# 复制JaCoCo agent到镜像中
COPY ./jacocoagent.jar /app/jacocoagent.jar

WORKDIR /app

EXPOSE 8000

# 设置JaCoCo代理参数
ENV JAVA_OPTS="-javaagent:/app/jacocoagent.jar=destfile=/app/jacoco-output/jacoco.exec,append=true,includes=*,output=file,jmx=true"
#ENV JAVA_OPTS="-javaagent:/app/jacocoagent.jar=output=tcpserver,address=*,port=6300
#CMD ["java", "$JAVA_OPTS", "-jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
