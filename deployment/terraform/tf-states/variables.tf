variable "tags" {
  type = map(string)
  description = "A map of tags to add to all resources"
}

variable "bucket_name" {
  type = string
    description = "The name of the S3 state storage bucket to create"
}

variable "db_name" {
  type = string
  description = "The DynamoDB lock table name"
}

variable "account_name" {
  type = string
  description = "Provide a name of the account, for users who use separate accounts for different environments you can use this to differentiate between them. E.g. dev, prod, test"
}

variable "region" {
  type = string
  description = "The region in which the resources will be created. E.g. eu-west-1"
}
