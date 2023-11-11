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

resource "aws_subnet" "public_1a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.public_subnet_cidr_block_1a
  availability_zone = "${var.region}a"
  tags              = merge(
    var.default_tags,
    { "Name" = "public-subnet-1a-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_subnet" "public_1b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.public_subnet_cidr_block_1b
  availability_zone = "${var.region}b"
  tags              = merge(
    var.default_tags,
    { "Name" = "public-subnet-1b-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_route_table_association" "public_internet_access_1a" {
  subnet_id      = aws_subnet.public_1a.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public_internet_access_1b" {
  subnet_id      = aws_subnet.public_1b.id
  route_table_id = aws_route_table.public_route_table.id
}