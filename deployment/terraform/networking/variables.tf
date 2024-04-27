## All Required Variables for the module
variable "default_tags" {
  type = map(string)
  description = "The tags to apply to all resources"
}

variable "region" {
  type = string
  description = "The region name in which the resources will be created, e.g. eu-west-1"
}

variable "account_name" {
  type = string
  description = "The name of the AWS account e.g. dev or prod"
}

variable "cidr_block" {
  type = string
  description = "The CIDR block to assign to the VPC, this must not clash with any existing IP addresses from other VPCs in your account"
}

variable "private_subnet_cidr_block_1a" {
  type = string
  description = "The CIDR block to assign to the private subnet within the VPC"
}

variable "private_subnet_cidr_block_1b" {
  type = string
  description = "The CIDR block to assign to the private subnet within the VPC"
}

variable "public_subnet_cidr_block_1a" {
  type = string
  description = "The CIDR block to assign to the public subnet within the VPC"
}

variable "public_subnet_cidr_block_1b" {
  type = string
  description = "The CIDR block to assign to the public subnet within the VPC"
}
