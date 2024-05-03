# Stress Testing Tool (Experimental)

<!-- TOC -->
* [Stress Testing Tool (Experimental)](#stress-testing-tool-experimental)
  * [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Configuration](#configuration)
    * [Installing & Running Locally](#installing--running-locally)
  * [Built With](#built-with)
<!-- TOC -->

This tool can be configured to start a specified amount of communication workflows.

- The tool will start the workflows and then wait for them to complete.
- Once complete the tool will shut down

> [!CAUTION]
> Due to limited development resources and costs I have not fully validated the tool with a large number of workflows.
> Run at your own risk, depending on your cloud configuration you may incur costs.

## Getting Started

Follow these instructions to run the project locally and connect it your local or cloud temporal instance. (note that
this application is not included in the helm deployment project)

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)

Ensure you have the following installed and configured locally or in the cloud:

- [Temporal Server 1.22.0 or newer](https://learn.temporal.io/getting_started/java/dev_environment/) running locally or
  in a development environment. See [helm deployment](../deployment/helm) to deploy the Temporal Server to a kubernetes
  cluster.
- [Kubernetes 1.27 or newer](https://kubernetes.io/releases/download/)
    -
    The [cluster context](https://kubernetes.io/docs/reference/kubectl/generated/kubectl_config/kubectl_config_set-context/)
    on your local machine should be set to the cluster you want to connect to.
- [Communication Worker](../communication-worker).
    - The worker must be running and listening to the same task queue as the stress test application is configured to
      send to.
- Communication gateway, see the [mock gateway](../mock-gateway) for a simple implementation.
    - The gateway must be deployed and registered with the [Gateway API](../gateway-api) to be able to start workflows.
        - Simply register the Gateway with the API and enter the endpoint and note down the UUID returned once created.

### Configuration

This section describes the configuration options available for the stress testing application via environment variables
or editing the `application.yml` file in src/main/resources.
Ensure you have completed the prerequisites before configuring the application.

| Environment Variable | Description                                                                                                                                   | Default Value          | Required |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|------------------------|----------|
| GATEWAY_ID           | The gateway ID registered with the [Gateway API](../gateway-api) to send communication requests to                                            |                        | Y        |
| USER_ID              | The user ID to send the communication to (If using the Mock gateway, use any string value here)                                               | stress-test-user       | N        |
| WORKFLOW_COUNT       | Number of workflows to start up and wait for completion                                                                                       | 1000                   | N        |
| TEMPORAL_TASK_QUEUE  | The task queue that the [Communication Worker](../communication-worker) is configured to read from                                            | communication-workflow | N        |
| TEMPORAL_NAMESPACE   | The namespace for the Temporal client to connect to                                                                                           | default                | N        |
| TEMPORAL_ENDPOINT    | The URL of the Temporal frontend endpoint to connect to                                                                                       | localhost:7233         | N        |
| ENCRYPTION_PASSWORD  | The password to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project | changeme               | N        |
| ENCRYPTION_SALT      | The salt to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project     | changeme               | N        |

### Installing & Running Locally

Follow the instructions below to get a development environment running:

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn spring-boot:run` to start the test.

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Temporal](https://temporal.io/)
