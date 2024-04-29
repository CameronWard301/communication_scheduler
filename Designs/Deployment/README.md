# Deployment Diagram
This section explains the different components of the cloud deployment.  
Use the [Terragrunt project](../../deployment/terragrunt) to deploy the infrastructure to AWS.

![diagram of AWS cloud deployment](DeploymentDiagram.png)

* EKS Compute Types
  * On Demand (can be bought with savings plan)
    * Temporal
  * 2 Spot Instances -  instantiate one replica of each pod on each instance, to ensure the high availability of the service.
    * Communication APIs (Auth, Schedule, History, Preferences, Gateway)
    * Web Management Portal
    * Portal BFF API
* Databases
  * Aurora Postgresql - Used for Temporal's Datastore
  * MongoDB - used to store the gateway table
  * DynamoDB - used to store the communication history table
