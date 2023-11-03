provider "aws" {
  region = var.region
  shared_credentials_files = ["~/.aws/credentials"]
  profile = "saml"

  default_tags {
    tags = local.common_tags
  }
}

terraform {
  required_providers {
    aws = {
      version = "~> 4.0"
      source  = "hashicorp/aws"
    }
  }
  required_version = ">= 1.0"
}


data "aws_caller_identity" "current" {}