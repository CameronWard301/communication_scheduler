# Integration Testing

This project is used to run end to end integration tests against all the following components. This runs automatically as a Kubernetes job when upgrading or installing the Kubernetes cluster using the [helm chart](../deployment/helm).
It takes about 7-10 minutes to run all the tests successfully.

> [!IMPORTANT]
> The integration tests read and write to the gateway database, and the Temporal cluster. Running this in production may modify user data and schedules if the tests are not configured correctly.


## Components tested:
- [History API](../history-api)
- [Auth API](../auth-api)
- [Data Converter API](../data-converter-api)
- [Schedule API](../schedule-api)
- [Gateway API](../gateway-api)
- [Preferences API](../preferences-api)
- [Web Portal](../web-portal)
- [Email Gateway](../email-gateway)
- [SMS Gateway](../sms-gateway)
- [Mock Gateway](../mock-gateway)
- [Communication Worker](../communication-worker) and [Communication Workflow](../workflows/communication_workflow)


## Getting Started

Follow these instructions to run the project locally and configure for Kubernetes deployment

### Prerequisites

Ensure you have the following installed and configured locally:
- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- IntelliJ IDEA with the [Cucumber plugin](https://www.jetbrains.com/help/idea/cucumber-support.html)


Ensure that the following are running locally or deployed to a cluster using the [helm chart](../deployment/helm):
- [Kubernetes 1.27 or newer](https://kubernetes.io/releases/download/)
    - A config map named `preferences` deployed to the cluster with the following keys:
        - `RetryPolicy`
        - `GatewayTimeoutSeconds`
        - See [helm deployment](../deployment/helm) to deploy the config map with default values
- [Temporal Server 1.22.0 or newer](https://learn.temporal.io/getting_started/java/dev_environment/) running locally or in a development environment. See [helm deployment](../deployment/helm) to deploy the Temporal Server to a kubernetes cluster.

- [Auth API](../auth-api)
- [History API](../history-api)
- [Schedule API](../schedule-api)
- [Gateway API](../gateway-api)
- [Preferences API](../preferences-api)
- [Data Converter API](../data-converter-api)
- [Web Portal](../web-portal)
- [Web Portal BFF](../web-portal-bff)
- [Selenium Grid Hub](https://www.selenium.dev/documentation/grid/) and [Chrome Node](https://hub.docker.com/r/selenium/node-chrome)
- [Mock Gateway](../mock-gateway)
- [Email Gateway](../email-gateway) (optional)
- [SMS Gateway](../sms-gateway) (optional)
[link](#add-using-mongodb-ui-simplest)
#### Configure Gateway Database for Integration Tests
If the gateways have not yet been added to the gateway database, follow these steps:  
##### Add using MongoDB UI (simplest):
1. Log into MongoDB online and navigate to the database: `communication-scheduling-platform`.
2. Create a new collection called `gateway` if it doesn't exist
3. Click on the `insert document` button and click the `{}` json format option
4. Add the following documents to the collection one by one for the mock, email and sms gateway:
5. ```json
   {
       "_id": "2cba1413-63b1-4c25-b13b-976c8794dd9b",
       "endpointUrl": "http://cs-mock-gateway-service.default.svc.cluster.local:8080/mock/message",
       "friendlyName": "Mock Gateway",
       "description": "always returns successful message regardless of user-id",
       "dateCreated": "2024-01-14T21:05:57.597846100Z",
       "_class": "io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway"
   }
   ``` 
6. ```json
   {
       "_id": "5e9500ff-59ac-4e0f-8e86-9cf21b6e500d",
       "endpointUrl": "https://cs-email-gateway-service.default.svc.cluster.local:8080/email/monthly-report/",
       "friendlyName": "email gateway",
       "description": "email gateway that sends monthly reports",
       "dateCreated": "2024-01-11T23:20:22",
       "_class": "io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway"
   }
   ```
7. ```json
   {
       "_id": "d2a3d6f8-2a67-471f-aa8f-75cbed07ebf9",
       "endpointUrl": "https://cs-sms-gateway-service.default.svc.cluster.local:8080/sms/weekly-report/",
       "friendlyName": "sms gateway",
       "description": "sms gateway that sends weekly reports",
       "dateCreated": "2024-01-11T23:20:22",
       "_class": "io.github.cameronward301.communication_scheduler.gateway_api.model.Gateway"
   }
   ```  
##### Add the Mock gateway to the gateway database using the gateway API or web portal (more complex):
1. The gateway can have any name or description but the URL must be the cluster service address or the local address of the mock gateway: E.g. `http://cs-mock-gateway-service.default.svc.cluster.local:8080/mock/message`
2. Note down the UUID that is generated for the gateway and set these as environment variables in the integration tests.
3. Repeat step 1 for the email and SMS gateway if you are testing these features.
    1. The email gateway URL should look like: `https://cs-email-gateway-service.default.svc.cluster.local:8080/email/monthly-report/`
    2. The sms gateway URL should look like: `https://cs-sms-gateway-service.default.svc.cluster.local:8080/sms/weekly-report/`



### Configuration

This section describes the configuration options for the integration testing project. These environment variables are set automatically in the [Kubernetes job configuration](../deployment/helm/templates/jobs.yaml).  
The easiest way to run this project locally is to set the environment variables in the `application.yml` file in the `src/test/resources` directory and run the required features using [IntelliJ](../https://www.jetbrains.com/idea/?var=1) with the [Cucumber plugin](https://www.jetbrains.com/help/idea/cucumber-support.html). 
- Note: you still need to add the MongoDB, encryption password and salt environment variables to the [IntelliJ run configuration](https://www.jetbrains.com/help/objc/add-environment-variables-and-program-arguments.html#add-environment-variables) to run locally.



| Environment Variable             | Description                                                                                                                                                                                         | Default Value                                                | Required                                                        | Used with filter              |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|-----------------------------------------------------------------|-------------------------------|
| TEMPORAL_NAMESPACE               | The namespace for the Temporal client to connect to                                                                                                                                                 | default                                                      | N                                                               |                               |
| KUBERNETES_NAMESPACE             | The namespace for the Kubernetes cluster to read the config map from                                                                                                                                | default                                                      | N                                                               |                               |
| TEMPORAL_TASK_QUEUE              | The task queue that the [worker](../communication-worker) is configured to run on                                                                                                                   | communication-workflow                                       | Y                                                               |                               |
| TEMPORAL_ENDPOINT                | The URL of the Temporal frontend endpoint to connect to                                                                                                                                             | localhost:7233                                               | Y                                                               |                               |
| ENCRYPTION_PASSWORD              | The password to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project (See [worker](../communication-worker) configuration) | changeme                                                     | Y                                                               |                               |
| ENCRYPTION_SALT                  | The salt to encrypt and decrypt Temporal payloads with - this must be the same for every Temporal encryption configuration in the project  (See [worker](../communication-worker) configuration)    | changeme                                                     | Y                                                               |                               |
| AUTH_API_ADDRESS                 | The URL of the auth API (Must end in /auth) to generate JWT tokens with for all tests.                                                                                                              | https://localhost:53655/auth                                 | Y                                                               |                               |
| MOCK_GATEWAY_ID                  | The mock gateway ID generated from prerequisite step 1.ii                                                                                                                                           | 2cba1413-63b1-4c25-b13b-976c8794dd9b                         | N                                                               | @MockGateway                  |
| SMS_GATEWAY_ID                   | The sms gateway ID generated from prerequisite step 1.ii                                                                                                                                            | d2a3d6f8-2a67-471f-aa8f-75cbed07ebf9                         | N                                                               | @SmsGateway                   |
| SMS_USER_1_ID                    | The ID of the user to start the communication workflow (by default this is set to the correct user ID that the gateway is setup with for user 1)                                                    | c96f0fdd-0029-4b3a-91e0-b93f2d93713d                         | N                                                               | @SmsGateway                   |
| EMAIL_GATEWAY_ID                 | The email gateway ID generated from prerequisite step 1.ii                                                                                                                                          | 5e9500ff-59ac-4e0f-8e86-9cf21b6e500d                         | N                                                               | @EmailGateway                 |
| EMAIL_USER_1_ID                  | The ID of the user to start the communication workflow (by default this is set to the correct user ID that the gateway is setup with for user 1)                                                    | 62f8a8e1-f55a-4d9a-ab15-852168a321a4                         | N                                                               | @EmailGateway                 |
| PREFERENCES_API_ADDRESS          | The URL of the preferences API (Must end in /preferences) to test the cluster preferences with.                                                                                                     | https://localhost:53933/preferences                          | Y if filter active                                              | @WebPortal or @PreferencesAPI |
| DATA_CONVERTER_API_ADDRESS       | The URL of the data converter API (Must end in /codec) to test encrypting and decrypting payloads with.                                                                                             | http://localhost:63458/codec                                 | Y if filter active                                              | @DataConverterAPI             |
| SCHEDULE_API_ADDRESS             | The URL of the schedule API (Must end in /schedule) to test schedule operations.                                                                                                                    | https://localhost:53286/schedule                             | Y if filter active                                              | @WebPortal or @ScheduleAPI    |
| HISTORY_API_ADDRESS              | The URL of the history API (Must end in /workflow) to test history operations.                                                                                                                      | https://localhost:50559/workflow                             | Y if filter active                                              | @WebPortal or @HistoryAPI     |
| GATEWAY_API_ADDRESS              | The URL of the gateway API (Must end in /gateway) to test gateway operations.                                                                                                                       | https://localhost:64854/gateway                              | Y if filter active                                              | @WebPortal or @GatewayAPI     |
| MONGODB_CONNECTION_STRING        | The connection string containing the username and password to connect to MongoDB                                                                                                                    | mongodb://localhost:27017                                    | Y if filter active. Should start with mongodb://                | @WebPortal or @GatewayAPI     |
| MONGODB_DATABASE_NAME            | The name of the mongo database to connect to                                                                                                                                                        | gateway-database-name                                        | Y if filter active                                              | @WebPortal or @GatewayAPI     |
| GATEWAY_API_ENTITY_ID            | Id prefix set to each gateway created in the gateway database by the integration tests.                                                                                                             | https://localhost:64854/gateway                              | Y if filter active                                              | @WebPortal or @GatewayAPI     |
| WEB_PORTAL_ADDRESS               | The URL web portal home page, by default this is the service URL that Selenium Grid will use. This is where you could set it to `localhost:5173` if running locally.                                | https://cs-web-portal-service.default.svc.cluster.local:3000 | Y if filter active                                              | @WebPortal                    |
| WEB_PORTAL_REMOTE_WEB_DRIVER_URL | The URL of the Selenium hub. This is only required if `WEB_PORTAL_ENVIRONMENT` is set to server. (Note it should end in /wd/hub)                                                                    | http://localhost:51127/wd/hub                                | Y if filter active or `WEB_PORTAL_ENVIRONMENT` is set to server | @WebPortal                    |
| WEB_PORTAL_ENVIRONMENT           | If using Selenium grid to set test the portal set to `server` if using your machines local browser set to `local`                                                                                   | server                                                       | Y if filter active                                              | @WebPortal                    |
| WEB_PORTAL_IMPLICIT_WAIT         | How long to set the [implicit wait](https://www.selenium.dev/documentation/webdriver/waits/#implicit-waits) for the selenium tests in seconds                                                       | 5                                                            | N                                                               | @WebPortal                    |
| WEB_PORTAL_EXPLICIT_WAIT         | How long to set the [explicit wait](https://www.selenium.dev/documentation/webdriver/waits/#explicit-waits) for the selenium tests in seconds                                                       | 10                                                           | N                                                               | @WebPortal                    |
| LOGGING_LEVEL                    | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project                             | info                                                         | N                                                               |                               |

### Installing & Running Locally

Follow the instructions below to use the testing tool to run the integration tests against the components in IntelliJ:
1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean compile` to build the project
4. Set up the application.yml file in `src/test/resources` with the required variables outlined above
5. Set up the MongoDB, encryption password and salt environment variables in the [IntelliJ run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration.html#change-template) for default Cucumber Java configurations.
6. Open a feature file located in `src/test/resources/` and click the play icon in the gutter to run the tests.
   1. Alternatively, you can right-click on the `src/test/resources` folder and click `Run 'All Features in resources'` to run all the tests.

### Running the project as a job on the cluster
This section describes running the tests on the cluster as a job and takes roughly 7 minutes to complete. This is done automatically when deploying the cluster using the [helm chart](../deployment/helm).

- Run the command from the project root to build and push a new image for both arm and amd platforms.
  - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`

- There is an extra environment variable called `TEST_FILTER_EXPRESSION` that is set in the [helm chart](../deployment/helm) values.yml file to run the tests with the correct filter expression.
  - See [Cucumber filter expression](https://cucumber.io/docs/cucumber/api/?lang=java#running-a-subset-of-scenarios) for the syntax to build your own filter expression to test specific components of the platform.

The available filter tags are:
- @EmailGateway
- @SmsGateway
- @MockGateway
- @InvalidGateway - checks that the workflow will time out when a gateway doesn't exist
- @DataConverterAPI
- @GatewayAPI
- @HistoryAPI
- @PreferencesAPI
- @ScheduleAPI
- @WebPortal

To run all the tests, set the `TEST_FILTER_EXPRESSION` to `"@EmailGateway or @SmsGateway or @MockGateway or @InvalidGateway or @DataConverterAPI or @GatewayAPI or @HistoryAPI or @PreferencesAPI or @ScheduleAPI or @WebPortal"`


## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Temporal](https://temporal.io/)
- [Cucumber](https://cucumber.io/)
- [Selenium](https://www.selenium.dev/)
