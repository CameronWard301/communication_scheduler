# used terraform module from: https://registry.terraform.io/modules/terraform-aws-modules/eks/aws/latest
# bootstrap user data adapted from: https://github.com/terraform-aws-modules/terraform-aws-eks/issues/2551

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.19.1"

  cluster_name                          = "communication-eks"
  cluster_version                       = var.cluster_version
  cluster_endpoint_private_access       = true
  cluster_endpoint_public_access        = true
  cluster_additional_security_group_ids = [aws_security_group.eks_cluster_internal.id]
  kms_key_administrators                = var.kms_key_administrators

  cluster_addons = {
    aws-ebs-csi-driver = {
      resolve_conflicts = "OVERWRITE"
      addon_version     = var.ebs_csi_driver_version

    }
    vpc-cni = {
      resolve_conflicts = "OVERWRITE"
      addon_version     = var.vpc_cni_version
    }
  }

  vpc_id     = var.vpc_id
  subnet_ids = var.eks_private_subnet_ids


  # EKS Managed Node Group(s)
  eks_managed_node_group_defaults = {

    disk_size              = 20
    instance_types         = ["t3a.medium"]
    vpc_security_group_ids = [aws_security_group.eks_nodes.id]
  }

  eks_managed_node_groups = {


    on_demand = {
      ami_type                     = var.on_demand_nodes.ami_type
      iam_role_additional_policies = {
        AmazonEBSCSIDriverPolicy = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy",
        DynamoDBPolicy           = aws_iam_policy.DynamoDBPolicy.arn
      }
      min_size     = var.on_demand_nodes.min_size
      max_size     = var.on_demand_nodes.max_size
      desired_size = var.on_demand_nodes.desired_size

      instance_types = var.on_demand_nodes.instance_types
      capacity_type  = "ON_DEMAND"
      labels         = {
        nodegroup = "on_demand_nodes"
      }
      enable_bootstrap_user_data = false

      # note that there is a bug with terraform where it will think this needs updating due to running it locally and through a pipeline
      # see: https://github.com/hashicorp/terraform-provider-aws/issues/5011
      pre_bootstrap_user_data = <<-EOT
        #!/bin/bash
        LINE_NUMBER=$(grep -n "KUBELET_EXTRA_ARGS=\$2" /etc/eks/bootstrap.sh | cut -f1 -d:)
        REPLACEMENT="\ \ \ \ \ \ KUBELET_EXTRA_ARGS=\$(echo \$2 | sed -s -E 's/--max-pods=[0-9]+/--max-pods=${var.on_demand_nodes.max_pods}/g')"
        sed -i '/KUBELET_EXTRA_ARGS=\$2/d' /etc/eks/bootstrap.sh
        sed -i "$${LINE_NUMBER}i $${REPLACEMENT}" /etc/eks/bootstrap.sh
      EOT

    }


    spot = {
      ami_type                     = var.spot_nodes.ami_type
      iam_role_additional_policies = {
        AmazonEBSCSIDriverPolicy = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy",
        DynamoDBPolicy           = aws_iam_policy.DynamoDBPolicy.arn
      }
      min_size     = var.spot_nodes.min_size
      max_size     = var.spot_nodes.max_size
      desired_size = var.spot_nodes.desired_size

      instance_types = var.spot_nodes.instance_types
      capacity_type  = "SPOT"
      labels         = {
        nodegroup = "spot_nodes"
      }
      enable_bootstrap_user_data = false

      # note that there is a bug with terraform where it will think this needs updating due to running it locally and through a pipeline
      # see: https://github.com/hashicorp/terraform-provider-aws/issues/5011
      pre_bootstrap_user_data = <<-EOT
        #!/bin/bash
        LINE_NUMBER=$(grep -n "KUBELET_EXTRA_ARGS=\$2" /etc/eks/bootstrap.sh | cut -f1 -d:)
        REPLACEMENT="\ \ \ \ \ \ KUBELET_EXTRA_ARGS=\$(echo \$2 | sed -s -E 's/--max-pods=[0-9]+/--max-pods=${var.spot_nodes.max_pods}/g')"
        sed -i '/KUBELET_EXTRA_ARGS=\$2/d' /etc/eks/bootstrap.sh
        sed -i "$${LINE_NUMBER}i $${REPLACEMENT}" /etc/eks/bootstrap.sh
      EOT

    }
  }


  tags = merge(
    var.default_tags,
    {}
  )
}

resource "aws_iam_policy" "DynamoDBPolicy" {
  policy = data.aws_iam_policy_document.DynamoDBPolicy.json
}

data "aws_iam_policy_document" "DynamoDBPolicy" {
  version = "2012-10-17"
  statement {
    actions = [
      "dynamodb:GetItem",
    ]
    resources = [
      "arn:aws:dynamodb:${var.region}:${data.aws_caller_identity.current.account_id}:table/gateway-db-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}"
    ]
  }
}