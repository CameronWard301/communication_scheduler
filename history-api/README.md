# History API

This is an API built with Spring Boot to retrieve workflow history and terminate running workflows.

## API Specification
See [swagger specification](https://app.swaggerhub.com/apis/CameronWard301/Communication_APIs/1.0.3#/History%20API) for the exposed endpoints.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Auth API](../auth-api) running locally or in a development environment
- [Temporal Server 1.22.0 or newer](https://learn.temporal.io/getting_started/java/dev_environment/) running locally or in a development environment. See [helm deployment](../deployment/helm) to deploy the Temporal Server to a kubernetes cluster.
### Configuration

This section describes the configuration options available for the history API via environment variables and spring profiles
- Run the project with the `ssl` profile to enable SSL
- To generate a self-signed keystore file for development purposes, run the following command in the project directory:
    - `keytool -genkeypair -alias history-api -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore history-api.p12 -validity 3650`
        - Follow prompts to set the keystore password.
        - Place the generated file in src/main/resources/keystore
    - If you don't want to place the file in the resources folder:
        - Run `base64 -e -i .\history-api.p12 -o history-api-base64.txt` to encode the keystore file for use in kubernetes secrets that will set the `HISTORY_API_SSL_KEY_STORE` to be the data in the text file you've just generated.

> [!CAUTION]
> Setting VERIFY_HOSTNAMES to false should NOT be used in a live production environment, it should only be used when testing with self-signed SSL certificates.

| Environment Variable               | Description                                                                                                                                                             | Default Value                                      | Required               |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------|------------------------|
| HISTORY_API_SSL_KEY_STORE_PASSWORD | The keystore password to access the keystore                                                                                                                            |                                                    | Y if using SSL profile |
| HISTORY_API_SSL_KEY_STORE          | The file path or file containing the public and private keys in PKCS12 format                                                                                           | classpath:keystore/history-api.p12                 | N                      |
| JWKS_URI                           | The endpoint of the [Auth API](../auth-api) to get the JWK to verify JWT tokens with                                                                                    | https://localhost:53655/auth/.well-known/jwks.json | N                      |
| VERIFY_HOSTNAMES                   | If using self signed certificates for SSL, set this to false for **non production environments only** for development                                                   | true                                               | N                      |
| TEMPORAL_NAMESPACE                 | The namespace for the Temporal client to connect to                                                                                                                     | default                                            | N                      |
| TEMPORAL_ENDPOINT                  | The URL of the Temporal frontend endpoint to connect to                                                                                                                 | localhost:7233                                     | N                      |
| ENCRYPTION_PASSWORD                | The password to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                           | changeme                                           | N                      |
| ENCRYPTION_SALT                    | The salt to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                               | changeme                                           | N                      |
| LOGGING_LEVEL                      | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project | info                                               | N                      |



### Installing & Running Locally

Follow the instructions below to get a development environment running:
1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build and test the project
4. Run `mvn spring-boot:run` to start the server
    - Run `mvn spring-boot:run -Dspring-boot.run.profiles=ssl` to start the server with SSL enabled (requires extra configuration)

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@HistoryAPI` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the History API to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Temporal](https://temporal.io/)
