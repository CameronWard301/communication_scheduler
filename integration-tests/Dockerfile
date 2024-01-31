# adapted from: https://docs.docker.com/language/java/run-tests/
FROM maven:3.9.5-amazoncorretto-21-debian
WORKDIR /app

COPY pom.xml ./
COPY src src

ENTRYPOINT ["mvn", "test", "-Dcucumber.filter.tags={$TEST_FILTER_EXPRESSION}"]


#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/communication-worker:latest --push .
