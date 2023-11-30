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

resource "aws_dynamodb_table_item" "mock-gateway" {
  count = var.deploy_mock_gateway_api ? 1 : 0
  hash_key   = aws_dynamodb_table.gateway_table.hash_key
  table_name = aws_dynamodb_table.gateway_table.name
  item       = <<ITEM
  {
    "id": { "S": "mock-gateway" },
    "endpoint_url": { "S": "${aws_api_gateway_stage.live-stage[count.index].invoke_url}/mock-gateway" },
    "friendly_name": { "S": "AWS Mock Gateway Endpoint" },
    "description": { "S": "Links to API gateway that will always return the userId passed to the request body and \"test-hash\" for the messageHash field" }

  }
  ITEM
}
