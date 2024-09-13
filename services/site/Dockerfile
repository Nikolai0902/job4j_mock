# Этап 1 - сборка проекта в jar
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir site
WORKDIR site
COPY . .
RUN mvn package -Dmaven.test.skip=true
CMD ["mvn", "liquibase:update", "-Pdocker"]

# Этап 2 - указание как запустить проект
FROM openjdk:17.0.2-jdk
WORKDIR site
COPY --from=maven /site/target/site-1.0.0.jar app.jar
CMD java -jar app.jar