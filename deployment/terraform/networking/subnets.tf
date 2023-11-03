resource "aws_subnet" "private_1a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_cidr_block_1a
  availability_zone = "${var.region}a"
  tags              = merge(
    var.default_tags,
    { "Name" = "private-subnet-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_subnet" "private_1b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_cidr_block_1b
  availability_zone = "${var.region}b"
  tags              = merge(
    var.default_tags,
    { "Name" = "private-subnet-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_route_table_association" "private_1a" {
  route_table_id = aws_route_table.main.id
  subnet_id      = aws_subnet.private_1a.id
}

resource "aws_route_table_association" "private_1b" {
  route_table_id = aws_route_table.main.id
  subnet_id      = aws_subnet.private_1b.id
}