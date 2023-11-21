# Resource configured from: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/dynamodb_table
resource "aws_dynamodb_table" "gateway_table" {
  name = "gateway-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  billing_mode = var.billing_mode
  deletion_protection_enabled = var.deletion_protection_enabled
  read_capacity = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = merge(
    var.default_tags,
    { Name = "gateway-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}