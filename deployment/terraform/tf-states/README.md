# Terraform state storage

This module creates the TF state storage in AWS S3 and DynamoDB.
It cannot be used with terragrunt and must be used manually by running the command `terraform apply` in this directory.

<!-- TOC -->
* [Terraform state storage](#terraform-state-storage)
  * [Pre-requisites](#pre-requisites)
  * [Configuration](#configuration)
  * [Usage](#usage)
  * [Next steps](#next-steps)
<!-- TOC -->

## Pre-requisites

1. An [AWS account](https://aws.amazon.com/resources/create-account/) with the necessary permissions to create
   resources.
    1. See [this guide](https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-files.html#cli-configure-files-format)
    on how to get your credentials and store them in the correct format.
2. [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html) installed and configured with the
   necessary credentials.
    1. Ensure your public and private keys are stored in the `~/.aws/credentials` file under a profile called `[saml]`
       for the Terragrunt project to access them.
3. [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli) installed.

## Configuration

1. Update the `terraform.tfvars` file with the desired values. I recommend using the default values provided to work
   automatically with the [Terragrunt project](../../terragrunt).
    1. `bucket_name` - The name of the S3 bucket to store the state files.
    2. `db_name` - The name of the DynamoDB table to store the state lock.
    3. `region` - The AWS region to create the resources in.
    4. `account_name` - The AWS profile to use to create the resources.
2. The bucket name created should look like: `terraform-state-<account_name>-<account_id>-<aws_region>`

## Usage

1. Run `terraform init` to initialize the project.
2. Run `terraform apply` to create the resources.
    1. Commit the `.tfstate` generated to version control.

> [!CAUTION]
> Run `terraform destroy` to delete these resources. This will delete all the terraform state storage. Terraform modules
> will no longer work without importing each resource.
> Only destroy once terraform has removed all the resources from other modules.

## Next steps

Deploy the rest of the infrastructure using the provided [Terragrunt project](../../terragrunt).
