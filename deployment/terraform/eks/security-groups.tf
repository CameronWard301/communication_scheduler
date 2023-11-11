resource "aws_security_group" "eks_cluster_internal" {
  name        = "eks-cluster-internal-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  description = "EKS Cluster Internal Security Group"
  vpc_id      = var.vpc_id

  tags = merge(
    var.default_tags,
    {
      "Name" = "communication-eks-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
    },
  )
}

resource "aws_security_group_rule" "eks_cluster_inbound_rule" {
  description              = "Allow nodes to connect with the cluster API server"
  from_port                = 443
  to_port                  = 443
  type                     = "ingress"
  protocol                 = "tcp"
  security_group_id        = aws_security_group.eks_cluster_internal.id
  source_security_group_id = aws_security_group.eks_nodes.id
}

resource "aws_security_group_rule" "eks_cluster_outbound_rule" {
  from_port                = 1024
  protocol                 = "tcp"
  security_group_id        = aws_security_group.eks_cluster_internal.id
  source_security_group_id = aws_security_group.eks_nodes.id
  to_port                  = 65535
  type                     = "egress"
}

resource "aws_security_group" "eks_nodes" {
  name        = "eks-nodes-sg-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
  description = "EKS Nodes Security Group"
  vpc_id      = var.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(
    var.default_tags,
    {
      "Name" = "eks-nodes-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
    },
  )
}

resource "aws_security_group_rule" "nodes_ingress_internal_rule" {
  from_port                = 0
  protocol                 = "-1"
  security_group_id        = aws_security_group.eks_nodes.id
  source_security_group_id = aws_security_group.eks_nodes.id
  to_port                  = 65535
  type                     = "ingress"
  description              = "Allow nodes to communicate with other nodes"
}

resource "aws_security_group_rule" "nodes_ingress_cluster_rule" {
  description              = "Allow nodes to receive instructions from control plane"
  from_port                = 1025
  protocol                 = "tcp"
  security_group_id        = aws_security_group.eks_nodes.id
  source_security_group_id = aws_security_group.eks_cluster_internal.id
  to_port                  = 65535
  type                     = "ingress"
}