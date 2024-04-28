# SMS Gateway

<!-- TOC -->
* [SMS Gateway](#sms-gateway)
  * [API Specification](#api-specification)
  * [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Configuration](#configuration)
    * [Installing & Running Locally](#installing--running-locally)
  * [Running the tests](#running-the-tests)
  * [Deployment](#deployment)
  * [Built With](#built-with)
<!-- TOC -->

- This is example SMS gateway built with Spring Boot and the [gateway library](../gateway-library) to send SMS messages to customers.  
- Use this project as a reference guide to integrate the gateway library into your own project.
- The gateway receives a user ID and generates a report containing antivirus and anti-malware statistics for the month and sends it to the user's phone number.  
- For simplicity, this gateway uses an in-memory database and the statistics are randomly generated when the gateway starts up.
The phone number for each user is passed in as an environment variable. See [configuration section](#configuration) for more details.
    - User 1:
        - ID: c96f0fdd-0029-4b3a-91e0-b93f2d93713d
        - First Name: Cameron
    - User 2:
        - ID: d1bb525d-5732-4396-b4de-c64baec8e3b4
        - First Name: Cameron


## API Specification
See [swagger specification for gateways](https://app.swaggerhub.com/apis/CameronWard301/Gateway_API/1.1.3) for request body and header requirements.
This gateway exposes the endpoint `POST /sms/weekly-report` to generate a weekly report and send it to a customer's phone number.

## Getting Started

Follow these instructions to run the project locally and configure for kubernetes deployment

### Prerequisites

Ensure you have the following installed or configured:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- History database (AWS DynamoDB) with the [schema defined here](../Designs/Images/Database/CommunicationHistoryDbSchema.svg)
  - Deploy one using the [Terragrunt library](../deployment/terragrunt)
- [Twilio Account](https://www.twilio.com/en-us/messaging/channels/sms) for SMS messaging
  - Create a free trial account
  - Once logged in, navigate to messaging -> try it out -> send an SMS and follow the [instructions](https://console.twilio.com/us1/develop/sms/try-it-out/send-an-sms) to get:
    - Account SID
    - Auth Token
    - From Phone Number - this is the number that the SMS will be sent from provided by Twilio
  - With a trial account, you will only be able to send messages to verified numbers such as your personal phone number.


### Configuration

This section describes the configuration options available for the gateway via environment variables and spring profiles
- Run the project with the `ssl` profile to enable SSL
- To generate a self-signed keystore file for development purposes, run the following command in the project directory:
    - `keytool -genkeypair -alias sms-gateway -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore sms-gateway.p12 -validity 3650`
        - Follow prompts to set the keystore password.
        - Place the generated file in src/main/resources/keystore
    - If you don't want to place the file in the resources folder:
        - Run `base64 -e -i .\sms-gateway.p12 -o sms-base64.txt` to encode the keystore file for use in kubernetes secrets that will set the `GATEWAY_API_SSL_KEY_STORE` to be the data in the text file you've just generated.

> [!CAUTION]
> Setting CORS_ENABLED or CSRF_ENABLED to false should NOT be used in a live production environment, it should only be used when testing with self-signed SSL certificates.

| Environment Variable                | Description                                                                                                                                                                                                                | Default Value                      | Required               |
|-------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|------------------------| 
| TWILIO_ACCOUNT_SID                  | Your Twilio Account SID, can be found by logging in and navigating to manage account -> general settings                                                                                                                   |                                    | Y                      |
| TWILIO_AUTH_TOKEN                   | Auth token provided by Twilio. Generate one by logging in and navigating to account -> API keys and tokens                                                                                                                 |                                    | Y                      | 
| TWILIO_FROM_PHONE_NUMBER            | The phone number created by Twilio to send your message. Do not include spaces or the "+" symbol, do include the country code. E.g. 4401234567890                                                                          |                                    | Y                      | 
| USER1_PHONE_NUMBER                  | The phone number to assign to User 1 (ID: c96f0fdd-0029-4b3a-91e0-b93f2d93713d). Note that this must be verified in Twilio if using a trial account                                                                        |                                    | Y                      | 
| USER2_PHONE_NUMBER                  | The phone number to assign to User 2 (ID: d1bb525d-5732-4396-b4de-c64baec8e3b4). Note that this must be verified in Twilio if using a trial account                                                                        |                                    | Y                      | 
| WORKER_API_KEY                      | A secret key to authenticate requests. Requests must include this key as a header labelled `x-worker-api-key`. This should be set to the value that the [worker](../communication-worker) will send in the request header. |                                    | Y                      | 
| REGION                              | The region of the DynamoDB table. E.g. "eu-west-1"                                                                                                                                                                         | eu-west-1                          | Y                      | 
| COMMUNICATION_HISTORY_TABLE_NAME    | The name of the DynamoDB table                                                                                                                                                                                             | communication-history              | Y                      | 
| SMS_GATEWAY_SSL_KEY_STORE_PASSWORD  | The keystore password to access the keystore                                                                                                                                                                               |                                    | Y if using SSL profile |
| SMS_GATEWAY_SSL_KEY_STORE           | The file path or file containing the public and private keys in PKCS12 format                                                                                                                                              | classpath:keystore/sms-gateway.p12 | N                      |
| TWILIO_POLLING_INTERVAL             | How long in seconds between each attempt to check if the message has been delivered                                                                                                                                        | 5                                  | N                      |
| TWILIO_MAXIMUM_POLLING_ATTEMPTS     | How many attempts to check if the message has been delivered. If there are no more attempts the Gateway returns a 500 error to Temporal                                                                                    | 10                                 | N                      |
| USER1_NAME                          | The first name to assign to User 1 (ID: c96f0fdd-0029-4b3a-91e0-b93f2d93713d) for use in the message sent                                                                                                                  | Cameron                            | N                      | 
| USER2_NAME                          | The first name to assign to User 2 (ID: d1bb525d-5732-4396-b4de-c64baec8e3b4) for use in the message sent                                                                                                                  | Cameron                            | N                      | 
| ROOT_LOGGING                        | The root [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the project                                                    | info                               | N                      |
| GATEWAY_LOGGING                     | The [logging level](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html#boot-features-logging-format) for the gateway library                                                 | info                               | N                      |
| CORS_ENABLED                        | Sets the CORS check when processing a request. Set to false for testing purposes to disable                                                                                                                                | true                               | N                      |
| CSRF_ENABLED                        | Sets the CSRF check when processing a request. Set to false for testing purposes to disable                                                                                                                                | true                               | N                      |


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
  and make sure that the `@SmsGateway` is added to the filter expression.

## Deployment

- Run the command from the project root to build and push a new image for both arm and amd platforms.
    - `docker buildx build --platform linux/amd64,linux/arm64 -t <account name>/<image-name>:<image-tag> --push .`
- See [helm deployment](../deployment/helm) to deploy the SMS Gateway to a kubernetes cluster.

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Gateway Library](../gateway-library)
- [Twilio](https://www.twilio.com/en-us/messaging/channels/sms)
