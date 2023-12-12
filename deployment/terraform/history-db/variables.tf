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

variable "billing_mode" {
  type = string // Can be either PROVISIONED or PAY_PER_REQUEST
}

//Optional Variables:

variable "deletion_protection_enabled" {
  type    = bool
  default = false
}

variable "read_capacity" {
  type    = number
  default = 5
}

variable "write_capacity" {
  type    = number
  default = 5
}

