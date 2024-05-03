# Terragrunt Deployment Project

<!-- TOC -->
* [Terragrunt Deployment Project](#terragrunt-deployment-project)
  * [Resources Deployed](#resources-deployed)
  * [How Terragrunt Configuration Works](#how-terragrunt-configuration-works)
  * [Pre-requisites](#pre-requisites)
    * [CLI tools:](#cli-tools)
    * [AWS pre-requisites:](#aws-pre-requisites)
    * [MongoDB pre-requisites:](#mongodb-pre-requisites)
  * [Configuration.](#configuration)
    * [MongoDB Atlas Authentication](#mongodb-atlas-authentication)
    * [AWS Authentication](#aws-authentication)
    * [Temporal Database Credentials](#temporal-database-credentials)
    * [Module Configurations:](#module-configurations)
  * [Deployment Steps](#deployment-steps)
    * [Applying & Destroying all modules](#applying--destroying-all-modules)
    * [Applying & Destroying individual modules](#applying--destroying-individual-modules)
  * [Common Issues](#common-issues)
  * [Automation with GitHub Actions](#automation-with-github-actions)
  * [Next Steps](#next-steps)
<!-- TOC -->

Use these instructions to deploy the cloud resources to AWS and MongoDB from your local machine. Ensure you follow the
pre-requisites before proceeding.
The section at the end of this readme talks about how automate the deployment using GitHub Actions. I recommend
deploying from a local machine first before automating the deployment.  
This project is configured to deploy resources in the AWS eu-west-1 region.

Use these instructions to deploy the cloud resources to AWS and MongoDB from your local machine. Ensure you follow the
pre-requisites before proceeding.
The section at the end of this readme talks about how automate the deployment using GitHub Actions. I recommend
deploying from a local machine first before automating the deployment.  
This project is configured to deploy resources in the AWS eu-west-1 region.

## Resources Deployed

See the [Terraform modules](../terraform) provided for more detail on the resources deployed.  
The resources deployed by this project are:

- Networking resources: VPC, security groups, public and private subnets, internet gateway and nat gateway
- DynamoDB table: for storing communication history data
- MongoDB cluster: for storing gateways
- Aurora Postgres Serverless V2: security groups and subnets for Temporal
- EKS cluster: node groups, autoscaling groups and security groups for creating the Kubernetes cluster

## How Terragrunt Configuration Works

Read more about Terragrunt [here](https://terragrunt.gruntwork.io/docs/getting-started/quick-start/).
See [this repository](https://github.com/gruntwork-io/terragrunt-infrastructure-live-example) for another example.  
In summary:

- Terragrunt uses the [Terraform modules](../terraform) provided to deploy the cloud resources to different AWS
  accounts, regions and environments.
- The `_envcommon` directory contains the common module configuration for all environments.
- Which AWS account, region and environment resources are deployed to is determined by the folder structure in this
  directory. It uses the hierarchy: environment -> account -> region -> modules.
    - The first folder is the account configuration, some users might have separate AWS accounts to separate
      environments. It contains the `account.hcl` file specifying the account id and the credentials profile to use.
    - The next folder is the region within the account to deploy the resources to e.g. eu-west-1, us-east-1. It contains
      the `region.hcl` file specifying the region to deploy to.
    - The last folder contains all the modules to deploy and which development environment the modules belong to in
      the `env.hcl` file.
        - Each module directory contains a `terragrunt.hcl` file that specifies the source of the module and any
          configuration variables to pass to the module that are not included in the common configuration.
- If you want to deploy to a new region within the dev account, simply create a new folder with the new region name and
  the `region.hcl` file.
    - Within new region directory, create an `env.hcl` file and create a "modules" directory. Then copy the modules you
      want to deploy to that region and configure any input variables.
- Terragrunt automatically works out the dependencies between modules and deploys them in the correct order.

> [!NOTE]  
> It is not possible to run a plan command before deploying all resources. You could run a plan command and deploy each
> module individually if you want to see the changes before deploying.

## Pre-requisites

### CLI tools:

1. [Terraform CLI](https://learn.hashicorp.com/tutorials/terraform/install-cli) installed.
2. [Terragrunt CLI](https://terragrunt.gruntwork.io/docs/getting-started/install/) installed.

### AWS pre-requisites:

1. An [AWS account](https://aws.amazon.com/resources/create-account/) with the necessary permissions to create
   resources.
    1. See [this guide](https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-files.html#cli-configure-files-format)
    on how to get your credentials and store them in the correct format.
2. [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html) installed and configured with the
   necessary credentials.
    1. Ensure your public and private keys are stored in the `~/.aws/credentials` file under a profile called `[saml]`
       for the Terragrunt project to access them.
3. Remote state storage - an S3 bucket and DynamoDB table to store the Terraform state (Recommended).
    1. Deploy using the [tf-states module provided](../terraform/tf-states). Otherwise, deploy manually using the
       following steps.
        1. This stores your Terraform state files in an S3 bucket and uses a DynamoDB table to lock the state files to
           prevent developers from making concurrent changes.
        2. [Create a new S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/userguide/create-bucket-overview.html)
           called `terraform-state-<account_name>-<account_id>-<aws_region>` replacing the name, id and region with the
           appropriate values as configured account and region hcl files.
        3. [Create a new DynamoDB table](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/getting-started-step-1.html)
           called `terraform-locks`.
        4. To change the name of the S3 bucket and DynamoDB table to use existing buckets, tables, or to use a local
           state configuration, update the remote state configuration block in the [terragrunt.hcl](terragrunt.hcl)
           file.

### MongoDB pre-requisites:

1. A [MongoDB Atlas account](https://www.mongodb.com/cloud/atlas) with the necessary permissions to create a cluster.
2. Create public and private keys within an
   organisation [using these instructions](https://www.mongodb.com/docs/atlas/configure-api-access/#grant-programmatic-access-to-an-organization).
    1. You may need to create an organisation if you do not have one.
    2. Make sure the keys generated have the `Ogranization Member` role.
    3. Note down the public and private keys and see the configuration section below on how to use them.

## Configuration.

1. **Set the MongoDB Atlas and AWS credentials as described in the configuration section below.**
2. **Set the Temporal database username and password using the configuration section below.**
3. **Set the account number in the `account.hcl` file in the `dev` directory to the AWS account number you are deploying
   to.**

### MongoDB Atlas Authentication

There are two ways to set the MongoDB Atlas credentials, using a variable file or environment variables

- Using a variable file
    - In the `/dev/eu-west-1/modules/gateway-db/` directory create a `terraform.tfvars` file.
    - Add the following content to the file and replace the placeholders with the actual values retrieved from the
      pre-requisites section:
      ```hcl
      mongo_private_key = "<private_key>" 
      mongo_public_key = "<public_key>"
      ```
- Using environment variables. Set these variables for CI/CD pipelines.
    - Set the following environment variables in your terminal:
      ```bash
      export TF_VAR_mongo_private_key="<private_key>"
      export TF_VAR_mongo_public_key="<public_key>"
      ```

### AWS Authentication

An AWS credential file should be used for local deployments and environment variables for CI/CD pipelines.

- Using a credential file:
    - Your AWS credentials should be stored in the `~/.aws/credentials` file under a profile called `[saml]`. Ensure you
      have the correct credentials stored in this file.
    - See [this guide](https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-files.html#cli-configure-files-format)
      for help
- Using environment variables. Set these variables for CI/CD pipelines.
    - Set the following environment variables in your terminal, replacing the placeholders with the actual values
      retrieved from the pre-requisites section:
      ```bash
      export AWS_ACCESS_KEY_ID="<access_key>"
      export AWS_SECRET_ACCESS_KEY="<secret_key>"
      ```

### Temporal Database Credentials

The Temporal database credentials can be set using a variable file or environment variables for CI/CD pipelines. You can
pick any username and password you want.

- Using a variable file
    - In the `/dev/eu-west-1/modules/temporal-db/` directory create a `terraform.tfvars` file.
    - Add the following content to the file and replace the placeholders with the actual values retrieved from the
      pre-requisites section:
      ```hcl
      temporal_db_password = "<password>" 
      temporal_db_username = "<username>"
      ```
- Using environment variables. Set these variables for CI/CD pipelines.
    - Set the following environment variables in your terminal:
      ```bash
      export TF_VAR_temporal_db_username ="<password>"
      export TF_VAR_temporal_db_password ="<username>"
      ```

### Module Configurations:

This section describes the optional configurations you can set for each module. The _envcommon directory contains the
common configuration for all cloud accounts and environments.
Further configurations can be set in the [Terraform modules](../terraform) provided.

| File location                                    | Parameter Name             | Description                                                                                                                                                                                                                                                                                                                                                            | Default Value                                                       |
|--------------------------------------------------|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| dev/account.hcl                                  | account_name               | The name of the account matching the folder                                                                                                                                                                                                                                                                                                                            | dev                                                                 |
| dev/account.hcl                                  | aws_account_id             | The account id of the AWS account you want to deploy the resources to                                                                                                                                                                                                                                                                                                  | 326610803524                                                        |
| dev/account.hcl                                  | aws_profile                | The aws profile to use (should be set to saml)                                                                                                                                                                                                                                                                                                                         | saml                                                                |
| _envcommon/gateway-db.hcl                        | mongo_db_project_name      | The name of the project to create in mongo DB                                                                                                                                                                                                                                                                                                                          | CSP                                                                 |
| default-tags.hcl                                 | default-tags               | A json object containing tags to apply to all cloud resources in key value pairs. <br/>The RepoURL is overridden automatically by modules to point to the URL of the GitHub repository for referencing which module created the resource.                                                                                                                              | "ManagedBy": "Terraform", "RepoURL": "Undefined"                    |
| dev/eu-west-1/modules/eks/terragrunt.hcl         | kms_key_administrators     | This is an array containing the IAM ARNs of roles or users that should have access to the EKS cluster                                                                                                                                                                                                                                                                  | arn:.../...AdministratorAccess                                      |
|                                                  | on_demand_nodes            | The configuration for the on demand nodes used for critical resources such as Temporal that must run 24/7.<br/>Specify here the architecture, instance, minimum number of nodes, maximum number of nodes and desired size.                                                                                                                                             | t4g.large, min_size 1, max size 5, desired size 1                   |
|                                                  | spot_nodes                 | The configuration for the spot nodes used for resources other than Temporal to save costs.<br/>Specify here the architecture, instance, minimum number of nodes, maximum number of nodes and desired size.                                                                                                                                                             | t4g.large, min_size 2, max size 5, desired size 2                   |
| dev/eu-west-1/modules/history-db/terragrunt.hcl  | billing_mode               | How the history table should be billed: [See here](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/dynamodb_table#billing_mode) for more detail                                                                                                                                                                                            | PAY_PER_REQUEST                                                     |
| dev/eu-west-1/modules/networking/terragrunt.hcl  | cidr_block                 | The [CIDR block](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc#cidr_block) to allocate to the VPC                                                                                                                                                                                                                                    | 172.32.0.0/16                                                       |
|                                                  | private and public subnets | The IP addresses to assign for each public and private subnet within the VPC                                                                                                                                                                                                                                                                                           | 172.32.1.0/24<br/>172.32.2.0/24<br/>172.32.3.0/24<br/>172.32.4.0/24 |
| dev/eu-west-1/modules/temporal-db/terragrunt.hcl | sg_eks_db_cidr             | Configures the security group to allow the specified CIDR block or IP addresses within the VPC to access the temporal database                                                                                                                                                                                                                                         | 172.32.0.0/16                                                       |
|                                                  | engine_version             | The postgres database version to deploy. It must be compatible with<br/>[Temporal's advanced visibility](https://docs.temporal.io/self-hosted-guide/visibility#postgresql) and<br/>[AWS postgres serverless versions](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/aurora-serverless.relnotes.html#aurora-serverless.relnotes.aurpostgres.serverless). | 13.12                                                               |

## Deployment Steps

This section describes how to plan, apply and destroy the cloud resources using Terragrunt.  
Ensure you have completed the pre-requisites and configuration steps above before proceeding.

### Applying & Destroying all modules

1. Clone this repository to your local machine.
2. Complete the pre-requisites and configuration steps above.
3. Navigate to the `deployment/terragrunt/dev/eu-west-1/modules` directory.
4. Run `terragrunt run-all apply` to deploy all the modules. Terragrunt will automatically deploy the modules in the
   correct order.
    1. Once you have deployed the resources, you can use `terragrunt run-all plan` to see the changes before applying
       them next time if needed.
5. Type `yes` when prompted to confirm the deployment.
6. To destroy all the resources, run `terragrunt run-all destroy` and type `yes` when prompted to confirm the
   destruction.

### Applying & Destroying individual modules

I recommend this only when troubleshooting, most of the time you should apply all modules at once to ensure dependencies
are also updated.

1. Clone this repository to your local machine.
2. Complete the pre-requisites and configuration steps above.
3. Navigate to the `deployment/terragrunt/dev/eu-west-1/modules` directory.
4. Navigate into the module you want to deploy such as `cd networking`.
5. Run `terragrunt apply` to deploy the module. Terragrunt will automatically deploy the module.
    1. Once you have deployed the resources, you can use `terragrunt plan` to see the changes before applying them next
       time if needed
6. Type `yes` when prompted to confirm the deployment.
7. To destroy the resources, run `terragrunt destroy` and type `yes` when prompted to confirm the destruction.

Follow this order when deploying the modules:

1. `networking`
2. `history-db`
3. `gateway-db`
4. `temporal-db`
5. `eks`

## Common Issues

- `ExpiredToken: The security token included in the request is expired` - Your AWS credentials have expired. Update
  the [saml] profile in the `~/.aws/credentials` file with the new credentials.
- `ParentFileNotFoundError: Could not find a account.hcl in any of the parent folders` - You need to run
  the `terragrunt run-all apply` command from the `modules` directory or `terragrunt apply` from within a specific
  module folder.
- `fatal: '$GIT_DIR' too big` - This can occur if the filepath is too long. Set the system environment
  variable: `TERRAGRUNT_DOWNLOAD` to a temporary directory with a shorter path. E.g. "C:\temp" on Windows.

## Automation with GitHub Actions

Once you have deployed the infrastructure, you can automate the deployment using GitHub Actions.  
See the [GitHub Action workflows](../../.github) provided in this repository for an example.

## Next Steps

See the [deployment readme](../README.md) to connect to the EKS cluster, finish configuring the cluster and deploy the
services.
