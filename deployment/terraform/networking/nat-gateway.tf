resource "aws_eip" "default" {
  vpc = true

  tags = merge (
    var.default_tags,
    { Name = "elastic-ip-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  })
}

resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.default.id
  subnet_id     = aws_subnet.public_1a.id

  tags = merge(
    var.default_tags,
    { Name = "nat-gateway-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}