# Communication Scheduling Platform
## Design diagrams:
* [System Context](#system-context-c1)
* [System Containers](#system-containers-c2)
* [System Components](#system-components-c3)
* [Class Diagrams](#class-diagrams-c4)
* [Activity Diagrams](#activity-diagrams)
* [Sequence Diagrams](#sequence-diagrams)
* [Database Schema](#database-schema)
* [Interface Diagrams](#interface-diagrams)
* [Deployment Diagram](Designs/Deployment/README.md)

### System Context (C1):
1. [System Context Diagram:](Designs/System%20Context/SystemContextDiagram.puml)  
    ![systemContext](Designs/Images/System%20Context/SystemContextDiagram.png)

### System Containers (C2):
2. [System Container Diagram:](Designs/System%20Context/SystemContainerDiagram.puml)  
    ![systemContainerDiagram](Designs/Images/System%20Context/SystemContainerDiagram.png)

### System Components (C3):
3. [Communication API Component:](Designs/System%20Context/Components/CommunicationAPIComponent.puml)
    ![ApiComponent](Designs/Images/System%20Context/Components/CommunicationAPIComponent.png)

4. [Worker Component:](Designs/System%20Context/Components/WorkerComponent.puml)  
    ![workerComponent](Designs/Images/System%20Context/Components/WorkerComponent.png)

### Class Diagrams (C4):
5. [Worker Component Class Diagram](Designs/System%20Context/Components/CommunicationWorkflowClassDiagram.puml)  
    ![Worker Component Class Diagram](Designs/Images/System%20Context/Components/WorkflowClassDiagram.png)

### Activity Diagrams:
6. [Send Communication Workflow](Designs/Activity%20Diagrams/SendCommunicationWorkflow.puml)  
    ![Send Communication Workflow](Designs/Images/Activity%20Diagrams/SendCommunicationWorkflow.png)

7. [GetSettingsActivity](Designs/Activity%20Diagrams/GetSettingsActivity.puml)  
    ![GetSettingsActivity](Designs/Images/Activity%20Diagrams/GetSettingsActivity.png)

8. [GetGatewayActivity](Designs/Activity%20Diagrams/GetGatewayActivity.puml)  
    ![GatGatewayActivity](Designs/Images/Activity%20Diagrams/GetGatewayActivity.png)

9. [SendMessageToGatewayActivity](Designs/Activity%20Diagrams/SendMessageToGatewayActivity.puml)  
    ![SendMessageToGatewayActivity](Designs/Images/Activity%20Diagrams/Send%20Message%20To%20Gateway%20Activity.png)

### Sequence Diagrams:
10. [Create New Workflow:](Designs/Sequence%20Diagrams/CreateNewWorkflow.puml)  
This describes a business user using the web portal to manually create a new communication schedule between a gateway and a user.  
    ![Create New Workflow](Designs/Images/Sequence%20Diagrams/Create%20New%20Workflow.png)

11. [Send Communication:](Designs/Sequence%20Diagrams/SendCommunication.puml) This demonstrates the messages transferred between temporal, the workers, the database and the communication gateway when executing a workflow.  
    ![Send Communication](Designs/Images/Sequence%20Diagrams/SendCommunication.png)

12. [Handle Send Communication Activity Error:](Designs/Sequence%20Diagrams/SendCommunicationHandleError.puml)  
This diagram demonstrates how temporal can retry an activity if one of the activities are not executing correctly 
    ![HandleActivityErrorSendCommunication](Designs/Images/Sequence%20Diagrams/SendCommunicationHandleError.png)
13. [Invoke Gateway:](Designs/Sequence%20Diagrams/InvokeGateway.puml)
The worker invokes the gateway to send a message to a user.
    [Invoke Gateway:](Designs/Images/Sequence%20Diagrams/InvokeGateway.png)

### Database Schema:
14. [GatewayDatabase](Designs/Database/gatewayDbSchema.puml)  
    ![GatewayDatabase](Designs/Images/Database/Gateway%20Database%20Schema.png)

### Interface Diagrams:
15. [Communication APIs](Designs/System%20Context/Components/ApiInterfaces.puml)  
    ![CommunicationAPIs](Designs/Images/System%20Context/Components/ApiInterfaces.png)

16. [Gateway References](Designs/System%20Context/Components/GatewayInterfaces.puml)  
    ![GatewayReferences](Designs/Images/System%20Context/Components/GatewayInterfaces.png)

17. [Worker Interfaces](Designs/System%20Context/Components/WorkerInterfaces.puml)  
    ![WorkerInterfaces](Designs/Images/System%20Context/Components/WorkerInterfaces.png)