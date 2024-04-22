terraform {
  required_providers {
    mongodbatlas = {
      source = "mongodb/mongodbatlas"
      version = "1.15.3"
    }
    aws = {
      version = "~> 4.0"
      source  = "hashicorp/aws"
    }
  }
}

provider "mongodbatlas" {
  private_key = var.mongo_private_key
  public_key = var.mongo_public_key
}
