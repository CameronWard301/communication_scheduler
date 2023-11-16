# Resource configured from: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/dynamodb_table
resource "aws_dynamodb_table" "gateway_table" {
  name = "gateway-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  billing_mode = var.billing_mode
  deletion_protection_enabled = var.deletion_protection_enabled
  read_capacity = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  range_key = "endpoint_url"
  hash_key = "id"

  local_secondary_index {
    name            = "FriendlyNameIndex"
    projection_type = "ALL"
    range_key       = "friendly_name"
  }

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "endpoint_url"
    type = "S"
  }

  attribute {
    name = "friendly_name"
    type = "S"
  }

  tags = merge(
    var.default_tags,
    { Name = "gateway-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}