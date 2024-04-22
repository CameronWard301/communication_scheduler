resource "mongodbatlas_advanced_cluster" "communication-scheduling-platform" {
  cluster_type = var.cluster_type
  name         = var.cluster_name
  project_id   = mongodbatlas_project.csp_project.id
  replication_specs {
    region_configs {
      backing_provider_name = "AWS"
      priority      = 7
      provider_name = "TENANT"
      region_name   = var.mongo_db_region_name
      electable_specs {
        disk_iops = 0
        node_count = 0
        instance_size = "M0"
      }
    }
    zone_name = "Zone 1"
  }
  tags {
    key = "environment"
    value = var.development_environment_tag
  }
  tags {
    key   = "managedBy"
    value = "Terraform"
  }
}
