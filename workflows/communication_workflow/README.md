# Communication Workflow 
This is the communication workflow that processes communication requests and sends them to the appropriate gateway.  
It has been uploaded to [Maven Central](https://central.sonatype.com/artifact/io.github.cameronward301.communication_scheduler.workflows/communication_workflow) and used in the [communication-worker](../../communication-worker) and [integration-tests](../../integration-tests) projects.

## Design
A communication workflow consists of the following activities and is responsible for invoking a gateway given a user ID and a gateway ID:
![Workflow Diagram](../../Designs/Images/Activity%20Diagrams/SendCommunicationWorkflow.svg)

1. Get the preferences to determine the retry policy of the next two activities
   ![GetPreferencesActivity](../../Designs/Images/Activity%20Diagrams/GetPreferencesActivity.svg)
2. Get the gateway - this returns a gateway object from MongoDB and extracts the endpoint URL to send the communication in the next activity.
   ![GetGatewayActivity](../../Designs/Images/Activity%20Diagrams/GetGatewayActivity.svg)
3. Send the communication  
   ![SendMessageToGatewayActivity](../../Designs/Images/Activity%20Diagrams/SendMessageToGatewayActivity.svg)

## Deployment
This can be deployed by running `mvn clean deploy` in the root of the project to upload a new version to Maven Central
