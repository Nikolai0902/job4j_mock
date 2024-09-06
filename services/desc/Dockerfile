FROM maven:3.6.3-openjdk-17

RUN mkdir decs

WORKDIR decs

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["mvn", "liquibase:update", "-Pdocker"]

CMD ["java", "-jar", "target/decs-1.0.0.jar"]