# Communication Worker

This is a [Temporal](https://temporal.io/) Spring Boot application that processes [communication workflows](../workflows/communication_workflow). 

## Design
A communication workflow consists of the following activities and is responsible for invoking a gateway given a user ID and a gateway ID:
![Workflow Diagram](../Designs/Images/Activity%20Diagrams/SendCommunicationWorkflow.svg)

1. Get the preferences to determine the retry policy of the next two activities
![GetPreferencesActivity](../Designs/Images/Activity%20Diagrams/GetPreferencesActivity.svg)  
2. Get the gateway - this returns a gateway object from MongoDB and extracts the endpoint URL to send the communication in the next activity.
![GetGatewayActivity](../Designs/Images/Activity%20Diagrams/GetGatewayActivity.svg)
3. Send the communication  
![SendMessageToGatewayActivity](../Designs/Images/Activity%20Diagrams/SendMessageToGatewayActivity.svg)

## Getting Started
A communication workflow library is available on [Maven Central](https://central.sonatype.com/artifact/io.github.cameronward301.communication_scheduler.workflows/communication_workflow) if you would like to use a different worker implementation.
To run this project locally or configure for cloud deployment follow the configuration steps below
### Prerequisites

To run locally, ensure you have the following installed:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Spring Boot 3.2.4 or newer](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot/3.2.4)
- [MongoDB Collection (Gateway Database)](https://www.mongodb.com/docs/atlas/getting-started/) running locally or in an Atlas free tier cluster. [See schema here](../Designs/Database/gatewayDbSchema.puml)
  - Deploy one using the [Terragrunt library](../deployment/terragrunt)
- History database (AWS DynamoDB by default) with the [schema defined here](../Designs/Images/Database/CommunicationHistoryDbSchema.svg)
  - Deploy one using the [Terragrunt library](../deployment/terragrunt)
- [Temporal Server 1.22.0 or newer](https://learn.temporal.io/getting_started/java/dev_environment/) running locally or in a cloud environment. See [helm deployment](../deployment/helm) to deploy the Temporal Server to a kubernetes cluster.


### Configuration

This section describes the configuration options available for the communication worker via environment variables.  
- You will need to create your own encryption password and salt to encrypt and decrypt the payloads sent to the Temporal server.
  - The same password and salt need to be added to the [integration-tests](../integration-tests), [data-converter](../data-converter-api), [schedule-api](../schedule-api) and [history-api](../history-api) to encrypt and decrypt the payloads.
- You will need to create your own API key password to send requests to your configured gateways in the `x-worker-api-key` header
  - The same API key must be used on each deployed [gateway](../gateway-library).
- **Changing the task queue (not recommended):** You can specify a different task queue than the default value , but this same value must be used in the [schedule-api](../schedule-api) project.
  - This is to ensure the schedule API can schedule the correct task queue for the worker to process.
  - However, the Grafana graphs for monitoring the platform will need updating to listen for the new queue.

> [!CAUTION]
> Setting SSL_VERIFICATION to false should NOT be used in a live production environment, it should only be used when testing with self-signed SSL certificates.

| Environment Variable      | Description                                                                                                                                                             | Default Value               | Required                       |
|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------|--------------------------------| 
| MONGODB_CONNECTION_STRING | The connection string containing the username and password to connect to MongoDB                                                                                        | mongodb://localhost:27017   | Y should start with mongodb:// |
| MONGODB_DATABASE_NAME     | The name of the mongo database to connect to                                                                                                                            | communication-database-name | Y                              | 
| GATEWAY_API_KEY           | The API key to authenticate gateway requests with                                                                                                                       | replace-me                  | Y                              | 
| SSL_VERIFICATION          | If using self signed certificates gateways, set this to false for **non production environments only** for development                                                  | true                        | N                              |
| TEMPORAL_NAMESPACE        | The namespace for the Temporal client to connect to                                                                                                                     | default                     | N                              |
| TEMPORAL_TASK_QUEUE       | The task queue to set for each schedule                                                                                                                                 | communication-workflow      | N                              |
| TEMPORAL_ENDPOINT         | The URL of the Temporal frontend endpoint to connect to                                                                                                                 | localhost:7233              | N                              |
| ENCRYPTION_PASSWORD       | The password to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                           | changeme                    | N                              |
| ENCRYPTION_SALT           | The salt to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project                               | changeme                    | N                              |
| WORKER_LOGGING            | The logging level for the worker components                                                                                                                             | info                        | N                              |
| ROOT_LOGGING              | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project | info                        | N                              |


## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- To run the integration tests see the [Integration Tests Project](../integration-tests)
  and make sure that the `@MockGateway`, `@EmailGateway` or `@SmsGateway` is added to the filter expression.
- 
## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the Communication Worker to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [MongoDB](https://www.mongodb.com/)
- [Temporal](https://temporal.io/)
