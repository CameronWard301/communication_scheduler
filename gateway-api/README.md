# Gateway API

This is an API built with Spring Boot to perform CRUD operations on the gateway database.

## API Specification
See [swagger specification](https://app.swaggerhub.com/apis/CameronWard301/Communication_APIs/1.0.3#/Communication%20Gateway%20API) for the exposed endpoints.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Auth API](../auth-api) running locally or in a development environment
- [MongoDB Collection](https://www.mongodb.com/docs/atlas/getting-started/) running locally or in a Atlas free tier cluster

### Configuration

This section describes the configuration options available for the gateway API via environment variables and spring profiles
- Run the project with the `ssl` profile to enable SSL
- To generate a self-signed keystore file for development purposes, run the following command in the project directory:
    - `keytool -genkeypair -alias <project-name> -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore <project-name>.p12 -validity 3650`
        - replace project desired value, follow prompts to set the keystore password.
        - Place the generated file in src/main/resources/keystore
    - If you don't want to place the file in the resources folder:
        - Run `base64 -e -i .\<project-name>.p12 -o <project-name>-base64.txt` to encode the keystore file for use in kubernetes secrets that will set the `GATEWAY_API_SSL_KEY_STORE` to be the data in the text file you've just generated.

> [!IMPORTANT]
> Setting VERIFY_HOSTNAMES to false should NOT be used in a live production environment, it should only be used when testing with self-signed SSL certificates.

| Environment Variable               | Description                                                                                                                                                             | Default Value                                      | Required                       |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------|--------------------------------| 
| MONGODB_CONNECTION_STRING          | The connection string containing the username and password to connect to MongoDB                                                                                        |                                                    | Y should start with mongodb:// |
| MONGODB_DATABASE_NAME              | The name of the mongo database to connect to                                                                                                                            |                                                    | Y                              | 
| GATEWAY_API_SSL_KEY_STORE_PASSWORD | The keystore password to access the keystore                                                                                                                            |                                                    | Y if using SSL profile         |
| GATEWAY_API_SSL_KEY_STORE          | The file path or file containing the public and private keys in PKCS12 format                                                                                           | classpath:keystore/gateway-api.p12                 | N                              |
| JWKS_URI                           | The endpoint of the [Auth API](../auth-api) to get the JWK to verify JWT tokens with                                                                                    | https://localhost:53655/auth/.well-known/jwks.json | N                              |
| VERIFY_HOSTNAMES                   | If using self signed certificates for SSL, set this to false for **non production environments only** for development                                                   | true                                               | N                              |
| LOGGING_LEVEL                      | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project | info                                               | N                              |



### Installing & Running Locally

A step by step series of examples that tell you how to get a development environment running.

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build and test the project
4. Run `mvn spring-boot:run` to start the server
    - Run `mvn spring-boot:run -Dspring-boot.run.profiles=ssl` to start the server with SSL enabled (requires extra configuration)

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@GatewayAPI` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Gateway API to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [MongoDB Spring JPA](https://spring.io/guides/gs/accessing-data-mongodb)
