locals {
  default-tags = jsonencode({
    "ManagedBy" : "Terraform",
    "RepoURL" : "Undefined"
  })
}
