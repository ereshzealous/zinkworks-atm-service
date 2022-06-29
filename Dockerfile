FROM openjdk:11.0.7-jdk

ADD build/libs/atm-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 9011
