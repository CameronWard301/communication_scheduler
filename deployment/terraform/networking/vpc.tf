resource "aws_vpc" "main" {
  cidr_block =  var.cidr_block
  tags = merge(
    var.default_tags,
    {"Name" = "vpc-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"},
  )

}