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
  type = bool
  default = false
}

variable "read_capacity" {
  type = number
  default = 5
}

variable "write_capacity" {
  type = number
  default = 5
}

variable "deploy_mock_gateway_api" {
  type = bool
  default = false
}

variable "enable_mock_gateway_logs" {
  type = bool
  default = false
}

variable "configure_global_api_gateway_log_role" {
  type = bool
  default = false
}
