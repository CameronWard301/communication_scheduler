# Adapted from example: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/rds_cluster#rds-serverless-v2-cluster
resource "aws_rds_cluster" "default" {
  cluster_identifier      = "temporal-${var.region}-${var.account_name}"
  availability_zones      = var.availability_zones
  backup_retention_period = var.backup_retention_period
  engine                  = "aurora-postgresql"
  engine_mode             = "serverless"
  engine_version          = var.engine_version
  database_name           = "temporal"
  master_username         = var.temporal_db_username
  master_password         = var.temporal_db_password
  preferred_backup_window = var.backup_window
  db_subnet_group_name    = aws_db_subnet_group.default.name
  vpc_security_group_ids  = [aws_security_group.db_sg.id]
  final_snapshot_identifier = "temporal-backup"

  scaling_configuration {
    auto_pause               = var.auto_pause
    max_capacity             = var.maximum_scaling
    min_capacity             = var.minimum_scaling
    seconds_until_auto_pause = var.auto_pause_delay
  }
  tags = merge(
    var.default_tags,
    { "Name" = "temporal-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}