resource "aws_db_subnet_group" "default" {
  subnet_ids = [var.private_subnet_id_1, var.private_subnet_id_2]
  tags       = merge(
    var.default_tags,
    {
      "Name" = "private-db-subnet-group-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
    },
  )
}