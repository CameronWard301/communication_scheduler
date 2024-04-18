# Mock Gateway

- This is example gateway built with Spring Boot and the [gateway library](../gateway-library) that returns the correct response to complete a [Communication Workflow](../workflows/communication_workflow).
- Use this project as a reference guide to integrate the gateway library into your own project.
- The gateway receives any user ID and returns a JSON response containing the User ID sent in the request body and the message hash to simulate a successful message delivery. 
- This application is useful for simulating a gateway that can be used to test the [communication workflow](../workflows/communication_workflow) without the need for a real message provider like Sendgrid
  - It can be configured to simulate message processing delays
  - This gateway can be used in the [stress testing application](../stress-test) to test the platform's ability to handle a large number of requests


## API Specification
See [swagger specification for gateways](https://app.swaggerhub.com/apis/CameronWard301/Gateway_API/1.1.3) for request body and header requirements.
This gateway exposes the endpoint `POST /mock/message` to simulate a successful message delivery.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed or configured:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- History database (AWS DynamoDB) with the [schema defined here](../Designs/Images/Database/CommunicationHistoryDbSchema.svg)
    - Deploy one using the [Terragrunt library](../deployment/terragrunt)


### Configuration

This section describes the configuration options available for the gateway via environment variables
The gateway has not been set up to use SSL for simplicity, see the [email gateway](../email-gateway) or the [SMS gateway](../sms-gateway) for an example of how to use SSL with a gateway.
> [!CAUTION]
> Setting CORS_ENABLED or CSRF_ENABLED to false should NOT be used in a live production environment, it should only be used when testing with self-signed SSL certificates.

| Environment Variable             | Description                                                                                                                                                                                                                | Default Value         | Required |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|----------| 
| WORKER_API_KEY                   | A secret key to authenticate requests. Requests must include this key as a header labelled `x-worker-api-key`. This should be set to the value that the [worker](../communication-worker) will send in the request header. |                       | Y        | 
| REGION                           | The region of the DynamoDB table. E.g. "eu-west-1"                                                                                                                                                                         | eu-west-1             | Y        | 
| COMMUNICATION_HISTORY_TABLE_NAME | The name of the DynamoDB table                                                                                                                                                                                             | communication-history | Y        | 
| MESSAGE_WAIT                     | How long the gateway waits before returning the successful response back to simulate a delivery delay. Measured in seconds.                                                                                                | 3                     | N        |
| ROOT_LOGGING                     | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project                                                    | info                  | N        |
| GATEWAY_LOGGING                  | The [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the gateway library                                                 | info                  | N        |
| CORS_ENABLED                     | Sets the CORS check when processing a request. Set to false for testing purposes to disable                                                                                                                                | true                  | N        |
| CSRF_ENABLED                     | Sets the CSRF check when processing a request. Set to false for testing purposes to disable                                                                                                                                | true                  | N        |


### Installing & Running Locally

A step by step series of examples that tell you how to get a development environment running.

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build and test the project
4. Run `mvn spring-boot:run` to start the server

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@MockGateway` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Mock Gateway to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Gateway Library](../gateway-library)
