resource "aws_security_group" "public_security_group" {
  name   =  "public-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  vpc_id = aws_vpc.main.id

  tags = merge(
    var.default_tags,
    { Name = "public-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}

resource "aws_security_group_rule" "ingress_public_443" {
  security_group_id = aws_security_group.public_security_group.id
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "ingress_public_80" {
  security_group_id = aws_security_group.public_security_group.id
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "egress_public" {
  security_group_id = aws_security_group.public_security_group.id
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}