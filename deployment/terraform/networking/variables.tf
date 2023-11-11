variable "default_tags" {
  type = map(string)
}

variable "region" {
  type = string
}

variable "account_name" {
  type = string
}

variable "cidr_block" {
  type = string
}

variable "private_subnet_cidr_block_1a" {
  type = string
}

variable "private_subnet_cidr_block_1b" {
  type = string
}

variable "public_subnet_cidr_block_1a" {
  type = string
}

variable "public_subnet_cidr_block_1b" {
  type = string
}