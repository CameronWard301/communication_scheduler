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
