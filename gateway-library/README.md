# Gateway Library

This is a library for building communication gateways.  
Download the library from [maven central](https://central.sonatype.com/artifact/io.github.cameronward301.communication_scheduler/gateway-library/overview).  

- Gateways are responsible for sending communications to users.
- For a given user ID, the gateway should resolve the user and content objects, generate the message, and send it to the user.
- This library allows business to use their existing customer databases and data models to send communications without having to change their existing systems. 
- The library uses a history database to store the hash of the message contents with the runID to prevent duplicate messages from being sent to the same user.
  - By default, this is an AWS DynamoDB database but this can be overridden, see the [implementation section](#creating-a-gateway-using-the-library) for more information.

## API Specification
Gateways should expose endpoints that accept and return JSON payloads outlined in the [swagger specification](https://app.swaggerhub.com/apis/CameronWard301/Gateway_API/1.1.3).
for gateways.
 - gateways should always expose POST endpoints
 - The endpoint path doesn't matter as you can create the gateway in the [gateway-api](../gateway-api) and specify the path there to match the one that the gateway exposes.
 - It must accept the JSON payload in the format specified in the swagger specification.
 - It must return a JSON payload in the format specified in the swagger specification.
 - Requests must be sent with a header `x-worker-api-key` that matches the one set in the [configuration section](#configuration) to be accepted

## Design
See the [interface diagram](../Designs/Images/System%20Context/Components/GatewayInterfaces.svg)
for more information on the design of the gateway library with an "Email Gateway" as an example.  
See the [sequence diagram](../Designs/Images/Sequence%20Diagrams/InvokeGateway.svg) to understand the communication between the gateway and systems it connects to
See the [activity diagram](../Designs/Images/Activity%20Diagrams/SendMessageToGatewayActivity.svg) to understand decision logic in the gateway library

## Getting Started
Add the library to your projects dependencies from [maven central](https://central.sonatype.com/artifact/io.github.cameronward301.communication_scheduler/gateway-library/overview).  
See the following example gateways for reference. 
- [Email Gateway](../email-gateway) - An example gateway that sends emails using Sendgrid
- [SMS Gateway](../sms-gateway) - An example gateway that sends SMS messages using Twilio
- [Mock Gateway](../mock-gateway) - An example gateway that pretends to send a message and always returns a 200 response for any given user ID

### Prerequisites

Ensure you have the following installed and configured locally:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.8.7 or newer](https://maven.apache.org/download.cgi)
- [Spring Boot 3.2.4 or newer](???)
- History database (AWS DynamoDB by default) with the [schema defined here](../Designs/Images/Database/CommunicationHistoryDbSchema.svg)
  - Deploy one using the [Terragrunt library](../deployment/terragrunt) 

### Creating a gateway using the library
1. Add this library to your project dependencies
2. Add [lombok](https://central.sonatype.com/artifact/org.projectlombok/lombok) to your project dependencies
3. Create or include your user and content models
   1. The content model must implement the `Content` interface and either be an entity or POJO for storing the information to send in the communication or generate the message with. (See the [email gateway](../email-gateway/src/main/java/io/github/cameronward301/communication_scheduler/email_gateway/model/EmailContent.java) for an example)
   2. The user model is for holding the information about the user to send the communication to. Note that it should have some kind of identifier, but the fields created here are up to you. (See the [email gateway](../email-gateway/src/main/java/io/github/cameronward301/communication_scheduler/email_gateway/model/EmailUser.java) for an example)
4. **Configuring the properties class.**  
   1. Create a public class that extends the `GatewayProperties` interface and use your User and Content models as the generic arguments.
   2. Annotate the top of the class with `@SuperBuilder` from lombok
   3. It should look like this with no body:
   ```java
      @SuperBuilder
      public class EmailGatewayProperties extends GatewayProperties<EmailUser, EmailContent> {
      }
      ```
5. **Resolving user and content objects from a user ID.**  
This class is responsible for resolving the user and content objects from the user ID passed in the request payload.
   1. Create a public class that implements the `UserContentService` interface and use your User and Content models as the generic arguments.  
   2. Implement the getUserAndContent method to resolve the user and content objects from the user ID.
   3. If you can resolve the content and user objects from just the user ID then you don't need to extend the `GetUserAndContent` interface. See the [sms gateway](../sms-gateway/src/main/java/io/github/cameronward301/communication_scheduler/sms_gateway/service/SmsUserContentService.java) for an example.
   4. If you need to use separate methods to resolve the user and content objects then extend the `GetUserAndContent` interface and implement the `getUser` and `getContent` methods. See the [email gateway](../email-gateway/src/main/java/io/github/cameronward301/communication_scheduler/email_gateway/service/EmailUserContentService.java) for an example.
   5. If you can't resolve the user by ID then throw a `ResourceNotFoundException`
6. **Creating a service class to generate and send the message contents.**  
This class is responsible for generating the message content and sending it to the user via your internal methods or third party APIs.  
   1. Create a public class that implements the `ContentDeliveryService` interface use the User and Content models as the generic arguments.  
Create a new class like this every time you want to generate a different type of message e.g. one for a monthly report and one for a weekly report that has different message templates.  
See the [email gateway](../email-gateway/src/main/java/io/github/cameronward301/communication_scheduler/email_gateway/service/EmailMonthlyReportContentDeliveryService.java) for an example.
   2. Implement the `sendContent` method that takes the User and Content objects and sends the message to the user.
   3. Throw a `ContentDeliveryException` if there is an error sending the message.
7. **Creating a controller to accept a request**  
This class configures the endpoints that the gateway will expose to accept requests.  
I recommend creating a new class for each message format you want to send. E.g. one for a weekly report and one from a monthly report  
See the [email gateway](../email-gateway/src/main/java/io/github/cameronward301/communication_scheduler/email_gateway/controller/EmailController.java) for an example.
   1. Create a public class that implements the `GatewayController` interface and use your User and Content models as the generic arguments.
   2. add the `@Controller`, `@ComponentScan("<base package name here>")`, and optionally a request mapping annotation to the top of this class
   3. add a private final field for the properties object you created in step 4
   4. add a private final field for the `CommunicationGatewayService` object provided by this library, use your User and Content models as the generic arguments
   5. Create a constructor that takes the following arguments:
      1. The delivery service object you created in step 6
      2. The user content service you created in step 5
      3. `DefaultCommunicationHistoryAccessProvider` object provided by this library (see section 8 below about using your own history database)
      4. The `CommunicationGatewayService` provided by this library and use your User and Content models as the generic arguments
      5. Initialise the properties object by calling the .builder() method on the class and setting the `userContentService`, `contendDeliveryService` and `communicationHistoryAccessProvider` fields with the objects you created in steps 5 and 6 respectively.
   6. Implement the `processGatewayRequest` method to accept the request payload and return the response payload.
      1. This should only need to include this line:  
      ```return GatewayController.sendCommunication(gatewayRequest, emailGatewayProperties, communicationGatewayService);```
8. **(Optional) Create your own history access provider**  
This class is responsible for checking if the message has already been sent and storing sent messages.  
See [configuration](#configuration) to disable the default dynamoDB client being initialised when running the gateway.
The database must have the [schema defined here](../Designs/Images/Database/CommunicationHistoryDbSchema.svg).
   1. Create a public class that implements the `CommunicationHistoryAccessProvider` interface
   2. Implement the methods:
      1. getPreviousCommunicationByMessageHash
      2. removeCommunicationHistoryByMessageHash
      3. storeCommunication
   3. Inject this dependency into your controller class and pass it to the properties object in the constructor to set it as the `communicationHistoryAccessProvider` parameter.


### Configuration

This section describes the configuration options that must be set in the application.properties or application.yml file of your project implementing this library

> [!CAUTION]
> Disabling the cors and csrf to false should NOT be used in a live production environment, it should only be used when testing.

All properties set must be under the base prefix: `io.github.cameronward301.communication-scheduler.gateway-library`

| Application Property                                      | Description                                                                                                                                                             | Default Value          | Required                           |
|-----------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------|------------------------------------|
| .worker.apiKey                                            | The API key that must be present in the header of all requests being sent to this gateway.                                                                              |                        | Y                                  |
| .default-communication-history-access-provider.table-name | The table name of the DynamoDB table to connect to                                                                                                                      |                        | Y if using default access provider |
| .default-communication-history-access-provider.region     | The AWS region of the DynamoDB table                                                                                                                                    |                        | Y if using default access provider |
| .default-communication-history-access-provider.enabled    | Only include if you want to set to false to use your own implementation                                                                                                 | true                   | N                                  |
| .security.cors.enabled                                    | Set to false for testing purposes to disable                                                                                                                            | true                   | N                                  |
| .security.csrf.enabled                                    | Set to false for testing purposes to disable                                                                                                                            | true                   | N                                  |

| Environment Variable               | Description                               | Default Value          | Required                           |
|------------------------------------|-------------------------------------------|------------------------|------------------------------------|
| AWS_ACCESS_KEY_ID                  | The AWS key id to connect to DynamoDB     |                        | Y if using default access provider |
| AWS_SECRET_ACCESS_KEY              | The AWS secret key to connect to DynamoDB |                        | Y if using default access provider |

## Running the tests

- To run the unit tests, run `mvn test` in the project directory
- See the specific gateway implementations and integration testing project for example unit tests and integration tests.

## Deployment

- This is deployed to Maven with the command `mvn clean deploy`

## Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [DynamoDB](https://aws.amazon.com/dynamodb/)
