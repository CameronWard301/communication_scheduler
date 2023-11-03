resource "aws_security_group" "db_sg" {
  name   = "eks-db-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  vpc_id = var.vpc_id
  //Only allow access from the EKS cluster through port 5432
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [var.sg_eks_db_cidr]
  }
  tags = merge(
    var.default_tags,
    { "Name" = "eks-db-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" },
  )
}