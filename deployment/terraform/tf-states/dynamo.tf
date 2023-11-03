resource "aws_dynamodb_table" "tfstate" {
  name           = var.db_name
  hash_key       = "LockID"
  billing_mode = "PAY_PER_REQUEST"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = merge(
    local.common_tags,
    tomap({
      "Name" = "terraform-locks"
    }
    )
  )
}