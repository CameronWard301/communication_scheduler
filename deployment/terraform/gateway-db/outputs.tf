output "gateway-db-id" {
  value = aws_dynamodb_table.gateway_table.id
}

output "gateway-db-arn" {
  value = aws_dynamodb_table.gateway_table.arn
}