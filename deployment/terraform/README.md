# Terraform Modules

This directory contains the Terraform modules used by the [Terragrunt project](../terragrunt) to deploy the cloud
resources to AWS.  
The intended use is to use Terragrunt to make any deployments, use these modules as a reference for modifying
configurations.

* The variables.tf file contains the input variables for the module.
    * The file is split into required and optional variables that can be overridden.
    * The description of each variable is provided as a comment.
    * Required variables are often handled by Terragrunt module dependencies in the `_envcommon` folder

See the available modules below:

* [EKS Cluster](eks) - Deploys an EKS cluster
* [Gateway DB](gateway-db) - Deploys a MongoDB Atlas cluster
* [History DB](history-db) - Deploys a DynamoDB table
* [Networking](networking) - Deploys a VPC, subnets, and security groups, internet gateway, and NAT gateway
* [Temporal DB](temporal-db) - Deploys an RDS Aurora PostgreSQL Serverless database.
* [tf-states](tf-states) - Deploys the Terraform state storage in AWS S3 and DynamoDB (not used with Terragrunt)
    * This should be deployed before using Terragrunt
