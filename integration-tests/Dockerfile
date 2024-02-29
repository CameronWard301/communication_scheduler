# adapted from: https://docs.docker.com/language/java/run-tests/
FROM maven:3.9.5-amazoncorretto-21-debian as build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn install -DskipTests

FROM maven:3.9.5-amazoncorretto-21-debian

COPY --from=build /app/target .
COPY --from=build /app/pom.xml .
COPY src src

CMD mvn test -Dcucumber.filter.tags="${TEST_FILTER_EXPRESSION}"

#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/cs-integration-tests:latest --push .
