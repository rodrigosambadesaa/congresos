FROM maven:3.9.9-eclipse-temurin-17 AS compilacion
# CVE-2026-114938: protect Maven while the JDK base is being updated.
ENV JAVA_TOOL_OPTIONS="-XX:-UseBASE64Intrinsics"
WORKDIR /proyecto

COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode clean package

FROM eclipse-temurin:17-jre-alpine
# CVE-2026-114938: disable the affected Base64 intrinsic in runtime.
ENV JAVA_TOOL_OPTIONS="-XX:-UseBASE64Intrinsics"
WORKDIR /app

COPY --from=compilacion /proyecto/target/congresos-1.0.0.jar app.jar

RUN addgroup -S java && adduser -S java -G java
USER java

ENTRYPOINT ["java", "-jar", "app.jar"]
