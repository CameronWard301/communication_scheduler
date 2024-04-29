//REQUIRED VARIABLES:
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

variable "billing_mode" {
  type = string // Can be either PROVISIONED or PAY_PER_REQUEST
  description = "The billing mode for the DynamoDB table."
}

//Optional Variables:

variable "deletion_protection_enabled" {
  type    = bool
  default = false
  description = "Enables deletion protection for the DynamoDB table."
}

variable "read_capacity" {
  type    = number
  default = 5
  description = "The read capacity for the DynamoDB table."
  # learn more here https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.ReadWriteCapacityMode.html
}

variable "write_capacity" {
  type    = number
  default = 5
  description = "The write capacity for the DynamoDB table."
  # learn more here https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.ReadWriteCapacityMode.html
}

