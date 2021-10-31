FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=build/libs/pacemaker-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /pacemaker_app.jar
ENTRYPOINT ["java", "-jar", "/pacemaker_app.jar"]