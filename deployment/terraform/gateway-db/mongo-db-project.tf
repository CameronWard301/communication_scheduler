resource "mongodbatlas_project" "csp_project" {
  name   = var.mongo_db_project_name
  org_id = data.mongodbatlas_roles_org_id.org_id.id
}

