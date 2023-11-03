# Terraform state storage
This module creates the TF stage storage in AWS S3 and DynamoDB.
It cannot be used with terragrunt and must be used manually by running the command `terraform apply` in the directory `deployment/terraform/tf-states`.