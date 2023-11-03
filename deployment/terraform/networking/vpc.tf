resource "aws_vpc" "main" {
  cidr_block = var.cidr_block
  tags       = merge(
    var.default_tags,
    { "Name" = "vpc-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_route_table" "main" {
  vpc_id = aws_vpc.main.id
}

resource "aws_main_route_table_association" "main" {
  vpc_id         = aws_vpc.main.id
  route_table_id = aws_route_table.main.id
}