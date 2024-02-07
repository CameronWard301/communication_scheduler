FROM maven:3.9.5-amazoncorretto-21-debian as build
WORKDIR /src/app

COPY src src
COPY pom.xml .

RUN mvn dependency:go-offline -B
RUN mvn package

FROM gcr.io/distroless/java21-debian12:latest
WORKDIR /app
COPY --from=build /src/app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/data-converter-api:latest --push .
