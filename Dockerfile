FROM maven:3.9.9-eclipse-temurin-17 AS compilacion
WORKDIR /proyecto

COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=compilacion /proyecto/target/congresos-1.0.0.jar app.jar

RUN addgroup -S java && adduser -S java -G java
USER java

ENTRYPOINT ["java", "-jar", "app.jar"]
