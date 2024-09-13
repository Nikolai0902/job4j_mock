# Этап 1 - сборка проекта в jar
FROM maven:3.6.3-openjdk-17 as maven
RUN mkdir auth
WORKDIR auth
COPY . .
RUN mvn package -Dmaven.test.skip=true
CMD ["mvn", "liquibase:update", "-Pdocker"]

# Этап 2 - указание как запустить проект
FROM openjdk:17.0.2-jdk
WORKDIR auth
COPY --from=maven /auth/target/auth-1.0.0.jar app.jar
CMD java -jar app.jar
