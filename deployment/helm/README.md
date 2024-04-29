# Helm Deployment
This directory contains the Helm chart for deploying the application.
The application can either be deployed in a local Kubernetes cluster or in a cloud-based Kubernetes cluster.

> [!CAUTION]
> * Do not use the example-secrets.yaml file in a production environment. It is only for demonstration purposes.
> * For production ensure that CORS, CSRF and verify SSL/hostnames are set to true
> * This configuration uses self-signed certificates for development purposes. In production, use a certificate from a trusted CA.

## Gateways:
* By default, the sms and email gateways are disabled. To enable them, set the `enabled` flag to `true` in the `values.yaml` file.
* Set the secrets needed in the `values-example-secrets.yaml` file
* Use the [email-gateway](../../email-gateway) and [sms-gateway](../../sms-gateway) projects to learn more about configuration needed

## Common Configuration:
This section describes the configuration for both local and cloud deployment.
* All the API components allow you to set the repository and tag for the image to use.
    * This allows you to deploy your own versions and test them in the cluster
* Components also allow you to set the logging level for the application
    * This can be set to `debug`, `info`, `warn`, `error` or `fatal`
* Components that have the `profile` configuration allow you to set the Spring profile to use
    * This can be set to `default`, or `ssl` for the components that you want to use SSL for
* `corsEnabled` and `csrfEnabled` allow you to enable or disable CORS and CSRF protection. By default, this is set to false for development.
* `verifyHostnames` allows you to enable or disable hostname verification for SSL. By default, this is set to false for development.


### Values Files
The following table describes the common configuration between EKS and local deployments that you may wish to change in the values-eks.yaml and values-local.yaml files.

| Configuration parameter/block            | Description                                                                                                                                                                                                            | Default Value                                                                                                                                 |
|------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| replicaCount                             | How many replicas of each deployment to create, set to 2 for  extra redundancy.                                                                                                                                        | 1                                                                                                                                             |
| image.pullPolicy                         | The [pull policy](https://kubernetes.io/docs/concepts/containers/images/#image-pull-policy) for all container images                                                                                                   | 1                                                                                                                                             |
| temporal.prometheus.enabled              | Deploys a prometheus instance to scrape temporal metrics, used for the grafana deployment                                                                                                                              | enabled: true                                                                                                                                 |
| temporal.schema.setup                    | Sets up the database schema automatically when deploying this helm chart. See [here](https://github.com/temporalio/helm-charts?tab=readme-ov-file#install-with-your-own-postgresql) for manual setup                   | enabled: true                                                                                                                                 |
| temporal.schema.update                   | Updates the database schema automatically when deploying this helm chart                                                                                                                                               | enabled: true                                                                                                                                 |
| grafana                                  | This configures the [grafana helm chart](https://github.com/grafana/helm-charts/blob/main/charts/grafana/README.md). You shouldn't need to change anything here.                                                       |                                                                                                                                               |
| communication_worker.temporal.taskQueue  | The task queue that the worker should listen to. Set this value on the other applications that need to start workflows on this task queue                                                                              | communication-workflow                                                                                                                        |
| communication_worker.temporal.namespace  | The Temporal namespace the worker should connect to. Set this value on the other applications that need to start workflows on this task queue                                                                          | communication-workflow                                                                                                                        |
| email_gateway.enabled                    | Whether or not to deploy the email gateway, its services and secrets. If set to true, follow the [email-gateway](../../email-gateway) configuration steps set the required secrets in your secrets file.               | false                                                                                                                                         |
| email_gateway.region                     | The AWS region that the history database is deployed in.                                                                                                                                                               | eu-west-1                                                                                                                                     |
| sms_gateway.enabled                      | Whether or not to deploy the SMS gateway, its services and secrets. If set to true, follow the [sms-gateway](../../sms-gateway) configuration steps set the required secrets in your secrets file.                     | false                                                                                                                                         |
| sms_gateway.region                       | The AWS region that the history database is deployed in.                                                                                                                                                               | eu-west-1                                                                                                                                     |
| sms_gateway.twilioPollingInterval        | How often to check Twilio API that the SMS has been sent in seconds                                                                                                                                                    | 5                                                                                                                                             |
| sms_gateway.maximumPollingAttempts       | How many attempts to check if the message has been delivered. If there are no more attempts the Gateway returns a 500 error to Temporal                                                                                | 10                                                                                                                                            |
| mock_gateway.region                      | The AWS region that the history database is deployed in.                                                                                                                                                               | eu-west-1                                                                                                                                     |
| preferences_api.clusterNamespace         | The namespace on the Kubernetes cluster that contains the preferences configmap                                                                                                                                        | default                                                                                                                                       |
| auth_api.tokenExpiration                 | How long before a token generated by the auth API expires in seconds                                                                                                                                                   | 86400                                                                                                                                         |
| schedule_api.temporalNamespace           | The namespace to create schedules in. This should be set to the same namespace that the worker is deployed in                                                                                                          | default                                                                                                                                       |
| history_api.temporalNamespace            | The namespace to query sent communications in. This should be set to the same namespace that the worker and schedule API                                                                                               | default                                                                                                                                       |
| web_bff.limitPeriodSeconds               | The rater limiter period to set in seconds. [See bff project](../../web-portal-bff) for more information                                                                                                               | 120                                                                                                                                           |
| web_bff.limitNumberRequests              | The number of requests per limit period a client can make. [See bff project](../../web-portal-bff) for more information                                                                                                | 1000                                                                                                                                          |
| web_portal.environment                   | The environment that the web portal is deployed in. This appends the JWT token stored in the browsers cookie with this value to differentiate between different environments.                                          | dev                                                                                                                                           |
| integration_tests.enableOnUpdate         | Run the integration tests whenever the `helm update` command is used. Only set one of the update or schedule enablers to true to prevent concurrent tests.                                                             | false                                                                                                                                         |
| integration_tests.enableOnSchedule       | Run the the integration tests according to the cron schedule.  Only set one of the update or schedule enablers to true to prevent concurrent tests.                                                                    | false                                                                                                                                         |
| integration_tests.cronSchedule           | How often to run the schedule if enableOnSchedule is set to true                                                                                                                                                       | "0 */2 * * *" # Every 2 hours                                                                                                                 |
| integration_tests.kubernetesNamespace    | The namespace on the Kubernetes cluster that contains the preferences configmap                                                                                                                                        | default                                                                                                                                       |
| integration_tests.temporalNamespace      | The namespace to create schedules in. This should be set to the same namespace that the worker and schedule API is deployed in                                                                                         | default                                                                                                                                       |
| integration_tests.filterExpression       | Tests to run matching the filter. See [integration-tests project](../../integration-tests) for available filters. By default the Email and SMS gateway tests are not enabled                                           | @GatewayAPI or @PreferencesAPI or @InvalidGateway or @DataConverterAPI or @MockGateway or @ScheduleAPI or @HistoryAPI or @WebPortal           |
| integration_tests.mockGatewayId          | The ID of the mock gateway entry in the gateway database. See [integration-tests project](../../integration-tests#add-using-mongodb-ui-simplest) to set this up. Use the MongoDB UI to use the default value set here  | 2cba1413-63b1-4c25-b13b-976c8794dd9b                                                                                                          |
| integration_tests.smsGatewayId           | The ID of the SMS gateway entry in the gateway database. See [integration-tests project](../../integration-tests#add-using-mongodb-ui-simplest) to set this up. Use the MongoDB UI to use the default value set here   | d2a3d6f8-2a67-471f-aa8f-75cbed07ebf9                                                                                                          |
| integration_tests.emailGatewayId         | The ID of the email gateway entry in the gateway database. See [integration-tests project](../../integration-tests#add-using-mongodb-ui-simplest) to set this up. Use the MongoDB UI to use the default value set here | 5e9500ff-59ac-4e0f-8e86-9cf21b6e500d                                                                                                          |
| integration_tests.selenium.implicitWait  | Set the selenium [implicit wait](https://www.selenium.dev/documentation/webdriver/waits/#implicit-waits) in seconds for the tests                                                                                      | 5                                                                                                                                             |
| integration_tests.selenium.explicitWait  | Set the selenium [explicit wait](https://www.selenium.dev/documentation/webdriver/waits/#explicit-waits) in seconds for the tests                                                                                      | 10                                                                                                                                            |
| PreferenceDefaults.GatewayTimeoutSeconds | Sets the default config map parameter for the gateway timeout. This value is only used if the config map doesn't yet exist. Go to the preferences page in the portal to learn more.                                    | 60                                                                                                                                            |
| PreferenceDefaults.RetryPolicy           | A JSON object representing the default retry policy. This value is only used if the config map doesn't yet exist. Go to the preferences page in the portal to learn more.                                              | { "maximumAttempts": "100", "backoffCoefficient": 2, "initialInterval": "PT1S", "maximumInterval": "PT100S", "startToCloseTimeout": "PT10S" } |

### Secrets File
The following section describes the secrets that need to be inserted into the `values-example-secrets.yaml` file.
1. Generate the secrets using the [Makefile](certs/README.md) in the certs directory
2. The new certificates will be placed in the certs directory
3. Fill in the placeholders with the correct values in the `values-example-secrets.yaml` file
    1. This includes all the keystores for the Java APIs, the web portal and bff certificates to enable ssl
    2. This also includes the Auth API public and private key for JWT generation

The table below describes the other secrets that need to be set in the `values-example-secrets.yaml` file.

| Configuration Parameter                         | Description                                                                                                                       | default value (if any)            |
|-------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|-----------------------------------|
| env.temporal.user                               | The username set for the temporal database from the [Terragrunt project](../terragrunt/README.md#temporal-database-credentials)   |                                   |
| env.temporal.password                           | The password set for the temporal database from the [Terragrunt project](../terragrunt/README.md#temporal-database-credentials)   |                                   |
| env.temporal.encryption.password                | Set a new encryption password to use for the worker, data converter, history and schedule api                                     |                                   |
| env.temporal.encryption.salt                    | Set a new encryption salt to use for the worker, data converter, history and schedule api                                         |                                   |
| env.communication_worker.apiKey                 | Set a new api key that the worker will send to gateways for authentication                                                        |                                   |
| env.communication_worker.mongo.databaseName     | The database name of the mongo DB. You shouldn't have to change this if you deployed using Terragrunt                             | communication-scheduling-platform |
| env.communication_worker.mongo.connectionString | The worker connection strings.  See [MongoDB Connection Strings](#mongodb-connection-strings)                                     |                                   |
| env.history_db.tableName                        | Copy the name of the DynamoDB history database from the AWS console that's generated from the [Terragrunt project](../terragrunt) |                                   |
| env.email_gateway                               | If enabled, see [Email gateway](../../email-gateway) for configuration options.                                                   |                                   |
| env.sms_gateway                                 | If enabled, see [SMS gateway](../../sms-gateway) for configuration options.                                                       |                                   |
| env.gateway_api.mongo.databaseName              | The database name of the mongo DB. You shouldn't have to change this if you deployed using Terragrunt                             | communication-scheduling-platform |
| env.gateway_api.mongo.connectionString          | The gateway API connection string. See [MongoDB Connection Strings](#mongodb-connection-strings)                                  |                                   |


### MongoDB Connection Strings:
Generate MongoDB connection strings for use in the worker and gateway api, follow these steps:  
You must have completed the [Terragrunt](../terragrunt) deployment first.
1. Go to the [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) console
2. Go to the CSP project
3. In the left hand menu, select `Database Access` under security
4. Click add new database user and add the following accounts for password authentication (create your own password)
    1. `cs-worker` with the custom `read-only` role
    2. `gateway-api` with the custom `read-write-communication-scheduling-platform` role
    3. If the roles are not available, choose one provided by MongoDB
5. In the left hand menu, select Database and then connect
6. Click drivers and select the Java driver
7. Note down the connection string and replace the password with the password you set for the users.

## Cloud EKS Deployment
Use this section to deploy to an AWS EKS cluster from your local machine.

### EKS Value configuration:
This section describes the settings in the `values-eks.yaml` file that for the EKS deployment.

| Configuration Parameter                                                                                      | Description                                                                                                                                                                                                                                                                                                                                                             | Default Value (if any) |
|--------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------|
| useAwsKubernetesSecret                                                                                       | Set to false for EKS deployment. EKS automatically handles AWS authentication through IAM roles                                                                                                                                                                                                                                                                         | false                  |
| temporal.xx.xx.affinity.nodeAffinity                                                                         | By default, Temporal resources are deployed onto the "on_demand_nodes" node group.<br/>If for some reason there are no on-demand nodes available it will use the spot nodes node group<br/>This ensures that Temporal will always run, if Spot nodes are removed. The admintools and web app are configured to run on spot nodes as these are non essential to temporal |                        |
| temporal.server.config.persistence.default.sql.host                                                          | Change this value to be the host of your RDS instance deployed through the [Terragrunt project](../terragrunt). You can find the host string by logging into the AWS console and looking at the RDS details. See [this guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_CommonTasks.Connect.html#CHAP_CommonTasks.Connect.EndpointAndPort) for help.  |                        |
| temporal.server.config.persistence.visibility.sql.host                                                       | Change this value to be the host of your RDS instance deployed through the [Terragrunt project](../terragrunt). You can find the host string by logging into the AWS console and looking at the RDS details. See [this guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_CommonTasks.Connect.html#CHAP_CommonTasks.Connect.EndpointAndPort) for help.  |                        |
| temporal.casandra<br/>temporal.mysql<br/>temporal.PostgreSQL<br/>temporal.grafana<br/>temporal.elasticsearch | These are all set to false as they're not needed or replaced by this helm charts own configuration E.g. grafana.                                                                                                                                                                                                                                                        | enabled: false         |



### Prerequisites
Ensure you have completed the following:
1. [Install helm](https://helm.sh/docs/intro/install/) on your local machine
2. [Install kubectl](https://kubernetes.io/docs/tasks/tools/) on your local machine
3. Clone this repository to your local machine
4. Deploy the cloud resources using the [Terragrunt project](../terragrunt)
5. Configure the EKS cluster by following the [Deployment readme](../README.md)
6. Generate the mongoDB connection strings by following [these steps](#mongodb-connection-strings)
7. Configure the values-eks.yaml file with the correct values
8. Configure the `values-example-secrets.yaml` file with the correct values
    1. Use this file as an example to create your own secrets file
    2. Don't commit the new file to the repository
9. Run `helm dependency update` to download the dependencies

### Deploying the resources:
1. Complete the eks deployment prerequisites.
2. Run the following command to deploy the resources:
    ```bash
    helm upgrade --install cs . -f ./values-eks.yaml -f ./values-example-secrets.yaml --wait --timeout=5m
    ```
   1. The first install will fail as Temporal tries to create the elastic search index when it already exists. Delete the `es-schema` job from the cluster to complete the installation. 
3. Use a monitoring tool like [Lens](https://k8slens.dev/) to monitor the resources
4. Follow the steps in the [integration tests project](../../integration-tests#add-using-mongodb-ui-simplest) to configure the Mock, Email and SMS gateway in the gateway database using the mongoDB UI.
    1. If using the web portal or gateway API to add the gateways. Update the ID in the mockGatewayId, emailGatewayId and smsGatewayId `values-eks.yaml` file to match the ID generated.
5. Optionally, enable the integration testing by setting the `enableOnUpdate` or `enableOnSchedule` flag to `true` in the `values-eks.yaml` file
    * Don't enable both flags at the same time to prevent concurrent tests
    * You will need to increase the --timeout value to be 20 minutes if enableOnUpdate is enabled.
6. Finish configuring temporal by following the steps in the [deployment readme](../README.md)

## Local deployment:
Use this section to deploy the application to a local Kubernetes cluster.  
The main difference between a local deployment and the EKS deployment is that a local cassandra and elasticsearch instances are also deployed to the cluster for Temporal to connect to.

### Local Value configuration:
This section describes the settings in the `values-local.yaml` file that are specific to the local deployment.

| Configuration Parameter                                     | Description                                                                                                      | Default Value (if any) |
|-------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|------------------------|
| useAwsKubernetesSecret                                      | Set to true for local deployments (see deploying resources section below)                                        | true                   |
| temporal.casandra<br/>temporal.elasticsearch                | This is set to true to deploy local instances of casandra and elasticsearch for Temporal's advanced visibility   | enabled: true          |
| temporal.mysql<br/>temporal.PostgreSQL<br/>temporal.grafana | These are all set to false as they're not needed or replaced by this helm charts own configuration E.g. grafana. | enabled: false         |

### Prerequisites
Ensure you have completed the following:
1. [Install helm](https://helm.sh/docs/intro/install/) on your local machine
2. [Install kubectl](https://kubernetes.io/docs/tasks/tools/) on your local machine
3. Clone this repository to your local machine
4. Deploy at least the history and gateway database resources using the [Terragrunt project](../terragrunt)
5. Generate the mongoDB connection strings by following [these steps](#mongodb-connection-strings)
6. Configure the values-local.yaml file with the correct values
7. Configure the `values-example-secrets.yaml` file with the correct values
    1. Use this file as an example to create your own secrets file
    2. Don't commit the new file to the repository
8. Run `helm dependency update` to download the dependencies
9. Create an AWS Access key for the resources to use. See [this guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html) for help
   1. The user should have permissions to read and write to the dynamodb message history table
10. Use the kubectl command to create the secret within your local cluster:
    1. ```bash
       kubectl create secret generic aws-credentials --from-literal=AWS_ACCESS_KEY_ID=<your-access-key-id> --from-literal=AWS_SECRET_ACCESS_KEY=<your-secret-access-key>
       ```
    2. This secret is not managed by the helm deployment to prevent it from being overridden if its updated.

### Deploying the resources:
1. Complete the local deployment prerequisites.
2. Run the following command to deploy the resources:
    ```bash
    helm upgrade --install cs . -f ./values-local.yaml -f ./values-example-secrets.yaml --wait --timeout=20m
    ```
3. Use a monitoring tool like [Lens](https://k8slens.dev/) to monitor the resources
4. Follow the steps in the [integration tests project](../../integration-tests#add-using-mongodb-ui-simplest) to configure the Mock, Email and SMS gateway in the gateway database using the mongoDB UI.
    1. If using the web portal or gateway API to add the gateways. Update the ID in the mockGatewayId, emailGatewayId and smsGatewayId `values-local.yaml` file to match the ID generated.
5. Optionally, enable the integration testing by setting the `enableOnUpdate` or `enableOnSchedule` flag to `true` in the `values-local.yaml` file
    * Don't enable both flags at the same time to prevent concurrent tests
6. Finish configuring temporal by following the steps in the [deployment readme](../README.md)


## Updating the resources:
If a new image is deployed, you can restart the deployments by running the following command.
Adjust the command to include the deployments you want to restart.
```bash
kubectl rollout restart deployment/cs-auth-api deployment/cs-gateway-api deployment/cs-communication-worker deployment/cs-data-converter-api deployment/cs-email-gateway deployment/cs-history-api deployment/cs-mock-gateway deployment/cs-preferences-api deployment/cs-schedule-api deployment/cs-sms-gateway deployment/cs-web-portal deployment/cs-web-portal-bff-api
```  

## Deployment pipeline:
- To automate the helm deployment, use the [GitHub Actions Readme](../../.github/workflows).
- Each value in the secrets file should be set as a secret in the GitHub repository and passed as an argument to the helm command
  - E.g. 
  ```bash
  helm upgrade cs ./deployment/helm/ --install --dry-run --namespace=default
    --values=./deployment/helm/values-eks.yaml
    --set "env.temporal.password=${{ secrets.TEMPORAL_DB_PASSWORD }}"
  ```
  - Where secrets.TEMPORAL_DB_PASSWORD is a secret in the GitHub repository

## Next Steps:
- Finish configuring temporal by following the steps in the [deployment readme](../README.md)

