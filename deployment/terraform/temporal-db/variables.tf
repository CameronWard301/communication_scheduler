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

variable "private_subnet_id_1" {
  type = string
  description = "The ID of the private subnet 1 created by the networking module"
}

variable "private_subnet_id_2" {
  type = string
  description = "The ID of the private subnet 2 created by the networking module"
}

variable "sg_eks_db_cidr" {
  type = string
  description = "The CIDR containing the IP addresses that are allowed to connect to the RDS instance from within the VPC"
}

variable "vpc_id" {
  type = string
  description = "The ID of the VPC created by the networking module"
}

variable "temporal_db_username" {
  type      = string
  sensitive = true
  description = "The username for the Temporal database"
}

variable "temporal_db_password" {
  type      = string
  sensitive = true
  description = "The password for the Temporal database"
}

//OPTIONAL VARIABLES:
variable "engine_version" {
  type    = string
  default = "13.9"
  description = "The version of the database engine"
}

variable "minimum_scaling" {
  type    = number
  default = 2
  description = "The minimum capacity of the database"
  # See https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/rds_cluster#min_capacity for more info
}

variable "maximum_scaling" {
  type    = number
  default = 16
  description = "The maximum capacity of the database"
  # See https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/rds_cluster#max_capacity for more info
}

variable "auto_pause" {
  type    = bool
  default = true
  description = "Whether to enable the Aurora Serverless auto-pause feature"
  # See https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/rds_cluster#auto_pause for more info
}

variable "auto_pause_delay" {
  type    = number
  default = 300 //min = 300, max = 86400
  description = "The time, in seconds, before the Aurora DB is paused after inactivity"
}

variable "backup_window" {
  type    = string
  default = "02:00-07:00"
  description = "The perferred time a backup takes place"
  # See more info here: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/rds_cluster#preferred_backup_window
}

variable "backup_retention_period" {
  type    = number
  default = 7
  description = "How long to retain a backup for"
}

variable "availability_zones" {
  type    = list(string)
  default = ["eu-west-1a", "eu-west-1b", "eu-west-1c"]
  description = "The availability zones in which the database will be created"
}

