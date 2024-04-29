resource "mongodbatlas_project" "csp_project" {
  name   = var.mongo_db_project_name
  org_id = data.mongodbatlas_roles_org_id.org_id.id
}

resource "mongodbatlas_project_ip_access_list" "aws_ip_access_list" {
  project_id = mongodbatlas_project.csp_project.id
  ip_address = var.aws_EIP_public_ip
  comment = "Allow AWS EIP To Access MongoDB"
}

