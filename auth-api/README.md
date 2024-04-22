# Auth API

This is an API built with Spring Boot to generate JWT tokens to authenticate with the following APIs:
- [History API](../history-api)
- [Gateway API](../gateway-api)
- [Schedule API](../schedule-api)
- [Preferences API](../preferences-api)

Use this as a reference to integrate into your identity provider (IdP).  
Right now the Auth API will generate a JWT token for any request. Therefore, any user who was EKS access can use this API to generate a JWT token to access the other APIs.

## API Specification
See [swagger specification](https://app.swaggerhub.com/apis/CameronWard301/Communication_APIs/1.0.3#/Authentication%20API) for the exposed endpoints.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Open SSL](https://www.openssl.org/source/)

### Configuration
This section describes the configuration options available for the auth API via environment variables and spring profiles

Generating the public and private keys for the Auth API requires the use of OpenSSL.
- `openssl genrsa -out ./private.key 4096`
- `openssl rsa -in private.key -pubout -outform PEM -out public.key`
- `openssl pkcs8 -topk8 -inform PEM -in private.key -out private_key.pem -nocrypt`
- This will generate a private_key.pem file and a public.key file. Use the contents of these to set the environment variable set out below



- Run the project with the `ssl` profile to enable SSL
- To generate a self-signed keystore file for development purposes, run the following command in the project directory:
    - `keytool -genkeypair -alias auth-api -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore auth-api.p12 -validity 3650`
        - Follow prompts to set the keystore password.
        - Place the generated file in src/main/resources/keystore
    - If you don't want to place the file in the resources folder:
        - Run `base64 -e -i .\auth-api.p12 -o auth-api-base64.txt` to encode the keystore file for use in kubernetes secrets that will set the `AUTH_API_SSL_KEY_STORE` to be the data in the text file you've just generated.

| Environment Variable            | Description                                                                                                                                                             | Default Value                   | Required               |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|------------------------|
| AUTH_API_PRIVATE_KEY            | The private key to verify the jwt with. This is the contents of the file "private_key.pem" generated above                                                              |                                 | Y                      |
| AUTH_API_PUBLIC_KEY             | The public key to sign the jwt with. This is the contents of the file "public.key" generated above                                                                      |                                 | Y                      |
| AUTH_API_SSL_KEY_STORE_PASSWORD | The keystore password to access the keystore                                                                                                                            |                                 | Y if using SSL profile |
| AUTH_API_SSL_KEY_STORE          | The file path or file containing the public and private keys in PKCS12 format                                                                                           | classpath:keystore/auth-api.p12 | N                      |
| AUTH_API_KEY_ID                 | The key ID to assign to each JWT token that is generated                                                                                                                | communication-auth-api          | N                      |
| AUTH_API_PORT                   | The port number that the server will run on                                                                                                                             | 8080                            | N                      |
| LOGGING_LEVEL                   | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project | info                            | N                      |


### Installing & Running Locally

Follow the instructions below to get a development environment running::

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build and test the project
4. Run `mvn spring-boot:run` to start the server
    - Run `mvn spring-boot:run -Dspring-boot.run.profiles=ssl` to start the server with SSL enabled (requires extra configuration)

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  The tests for the auth API are part of the History, Gateway, Schedule and Preferences integration tests. See these projects for the required filter annotation needed to run them in the integration test.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Auth API to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Nimbus JOSE + JWT (JWK)](https://connect2id.com/products/nimbus-jose-jwt/examples/jwk-generation)
- []
