FROM openjdk:8
ADD target/train-service.jar train-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "train-service.jar"]