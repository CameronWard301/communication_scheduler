# adapted from: https://docs.docker.com/language/java/run-tests/
FROM maven:3.9.5-amazoncorretto-21-debian
WORKDIR /app

#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
COPY pom.xml ./
RUN mvn dependency:resolve
COPY src src

ENTRYPOINT ["mvn", "test"]

#RUN mvn dependency:go-offline -B
#RUN mvn package -DskipTests
#
#FROM gcr.io/distroless/java21-debian12:latest
#WORKDIR /app
#COPY --from=build /src/app/target/*.jar /app/app.jar

#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/communication-worker:latest --push .
