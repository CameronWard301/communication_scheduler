resource "aws_internet_gateway" "public_gateway" {
  vpc_id = aws_vpc.main.id

  tags = merge(
    var.default_tags,
    { "Name" = "igw-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"}
  )
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.public_gateway.id
  }

  tags = merge(var.default_tags,
    { Name = "public-route-table-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}

resource "aws_route" "public_route" {
  route_table_id = aws_route_table.main.id
  nat_gateway_id = aws_nat_gateway.main.id
  destination_cidr_block = "0.0.0.0/0"
}