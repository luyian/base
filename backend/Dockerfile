FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN mkdir -p /data/upload /data/export /app/logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
