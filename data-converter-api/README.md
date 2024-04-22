# Temporal Data Converted API

This is an API built with Spring Boot to connect to the Temporal Web UI to decode payloads 

## API Specification
See the temporal documentation for the [API contract](https://docs.temporal.io/production-deployment/data-encryption#api-contract-specifications) This application exposes the `/codec/encode` and `/codec/decode` endpoints.

## Getting Started

This project is [deployed](../deployment/helm) on the cluster and can be forwarded to your local machine with the Temporal UI.
See the [temporal codec server guide](https://docs.temporal.io/production-deployment/data-encryption#set-your-codec-server-endpoints-with-web-ui-and-cli) on how to find the codec server configuraiton seetings for the Web UI and CLI.
- In the Web UI, set the codec server to `http://localhost:<port_number>/codec`.

Follow the instructions below to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Temporal Server 1.22.0 or newer](https://learn.temporal.io/getting_started/java/dev_environment/) running locally or in a development environment. See [helm deployment](../deployment/helm) to deploy the Temporal Server to a kubernetes cluster.

### Configuration
This section describes the configuration options available for the data converter API via environment variables.

| Environment Variable | Description                                                                                                                                                             | Default Value | Required |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|----------| 
| ENCRYPTION_PASSWORD  | The password to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                           | change-me     | Y        |
| ENCRYPTION_SALT      | The salt to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                               | chang-me      | Y        |
| LOGGING_LEVEL        | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project | info          | N        |



### Installing & Running Locally

Follow the instructions below to get a development environment running:
1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build and test the project
4. Run `mvn spring-boot:run` to start the server

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@DataConverterAPI` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the data converter API to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Temporal](https://temporal.io/)
