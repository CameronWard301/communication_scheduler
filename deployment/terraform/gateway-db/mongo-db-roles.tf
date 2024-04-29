resource "mongodbatlas_custom_db_role" "read-write" {
  project_id = mongodbatlas_project.csp_project.id
  role_name  = "read-write-communication-scheduling-platform"

  inherited_roles {
    database_name = mongodbatlas_advanced_cluster.communication-scheduling-platform.name
    role_name     = "readWrite"
  }

}

resource "mongodbatlas_custom_db_role" "read-only" {
  project_id = mongodbatlas_project.csp_project.id
  role_name  = "read-only"

    actions {
      action = "FIND"
      resources {
        cluster = false
        collection_name = "gateway"
        database_name = mongodbatlas_advanced_cluster.communication-scheduling-platform.name
      }
    }
}
