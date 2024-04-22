//REQUIRED VARIABLES:
variable "development_environment_tag" {
  type = string
}

variable "mongo_public_key" {
  type = string
}

variable "mongo_private_key" {
  type = string
}

//Optional Variables:

variable "cluster_type" {
  type = string
  default = "REPLICASET"
}

variable "cluster_name" {
  type = string
  default = "communication-scheduling-platform"
}

variable "mongo_db_project_name" {
  type = string
  default = "CSP"
}

variable "mongo_db_region_name" {
  type = string
  default = "EU_WEST_1"
}
