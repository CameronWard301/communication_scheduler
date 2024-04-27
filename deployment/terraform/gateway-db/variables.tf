//REQUIRED VARIABLES:
variable "development_environment_tag" {
  type = string
  description = "The type of environment this is, e.g. dev, test, prod"
}

variable "mongo_public_key" {
  type = string
  description = "Your MongoDB public key, see: https://www.mongodb.com/docs/atlas/configure-api-access/#grant-programmatic-access-to-services"
}

variable "mongo_private_key" {
  type = string
  description = "Your MongoDB private key, see: https://www.mongodb.com/docs/atlas/configure-api-access/#grant-programmatic-access-to-services"

}

//Optional Variables:

variable "cluster_type" {
  type = string
  default = "REPLICASET"
  description = "The type of cluster to create, e.g. REPLICASET, SHARDED"
  # See: https://www.terraform.io/docs/providers/mongodbatlas/r/cluster.html#cluster_type
}

variable "cluster_name" {
  type = string
  default = "communication-scheduling-platform"
  description = "The name of the cluster to create"
  # I don't recommend changing this
}

variable "mongo_db_project_name" {
  type = string
  default = "CSP"
  description = "The name of the project to create"
}

variable "mongo_db_region_name" {
  type = string
  default = "EU_WEST_1"
  description = "The region to create the cluster in"
  # Must be all caps and with underscores
}
