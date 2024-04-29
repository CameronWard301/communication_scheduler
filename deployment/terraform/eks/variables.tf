variable "default_tags" {
  type = map(string)
}

variable "region" {
  type = string
}

variable "account_name" {
  type = string
}

variable "vpc_id" {
  type = string
}


variable "eks_private_subnet_ids" {
  type = list(string)
}

#### Optional variables ####
variable "cluster_version" {
  type    = string
  default = "1.29"
}

variable "kms_key_administrators" {
  type    = list(string)
  default = []
}

variable "ebs_csi_driver_version" {
  type    = string
  default = "v1.30.0-eksbuild.1"
}

variable "vpc_cni_version" {
  type    = string
  default = "v1.15.4-eksbuild.1"
}

variable "on_demand_nodes" {
  type = object({
    ami_type       = string
    instance_types = list(string)
    min_size       = number
    max_size       = number
    desired_size   = number
    max_pods       = number
  })
  default = {
    ami_type       = "AL2_x86_64"
    instance_types = ["t4g.medium"]
    min_size       = 1
    max_size       = 2
    desired_size   = 1
    max_pods       = 110
  }
}

variable "spot_nodes" {
  type = object({
    ami_type       = string
    instance_types = list(string)
    min_size       = number
    max_size       = number
    desired_size   = number
    max_pods       = number
  })
  default = {
    ami_type       = "AL2_x86_64"
    instance_types = ["t4g.medium"]
    min_size       = 2
    max_size       = 2
    desired_size   = 1
    max_pods       = 110
  }
}

