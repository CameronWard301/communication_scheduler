//REQUIRED VARIABLES:
variable "default_tags" {
  type = map(string)
}

variable "region" {
  type = string
}

variable "account_name" {
  type = string
}

variable "private_subnet_id_1" {
  type = string
}

variable "private_subnet_id_2" {
  type = string
}

variable "sg_eks_db_cidr" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "temporal_db_username" {
  type      = string
  sensitive = true
}

variable "temporal_db_password" {
  type      = string
  sensitive = true
}

//OPTIONAL VARIABLES:
variable "engine_version" {
  type    = string
  default = "13.9"
}

variable "minimum_scaling" {
  type    = number
  default = 2
}

variable "maximum_scaling" {
  type    = number
  default = 16
}

variable "auto_pause" {
  type    = bool
  default = true
}

variable "auto_pause_delay" {
  type    = number
  default = 300 //min = 300, max = 86400
}

variable "backup_window" {
  type    = string
  default = "02:00-07:00"
}

variable "backup_retention_period" {
  type    = number
  default = 7
}

variable "availability_zones" {
  type    = list(string)
  default = ["eu-west-1a", "eu-west-1b", "eu-west-1c"]
}

