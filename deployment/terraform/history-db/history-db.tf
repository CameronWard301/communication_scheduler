# Resource configured from: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/dynamodb_table
resource "aws_dynamodb_table" "history_table" {
  name                        = "history-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  billing_mode                = var.billing_mode
  deletion_protection_enabled = var.deletion_protection_enabled
  read_capacity               = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity              = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  hash_key                    = "message_hash"

  attribute {
    name = "message_hash"
    type = "S"
  }

  tags = merge(
    var.default_tags,
    { Name = "history-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}
