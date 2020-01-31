FROM openjdk:8
ADD target/reservation-service.jar reservation-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "reservation-service.jar"]