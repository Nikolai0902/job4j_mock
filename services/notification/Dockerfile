# Этап 1 - сборка проекта в jar
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir notification
WORKDIR notification
COPY . .
RUN mvn package -Dmaven.test.skip=true
CMD ["mvn", "liquibase:update", "-Pdocker"]

# Этап 2 - указание как запустить проект
FROM openjdk:17.0.2-jdk
WORKDIR notification
COPY --from=maven /notification/target/notification-1.0.0.jar app.jar
CMD java -jar app.jar