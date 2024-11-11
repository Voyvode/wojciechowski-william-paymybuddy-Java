FROM openjdk:21-jdk-slim
COPY target/paymybuddy-1.0.jar app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
