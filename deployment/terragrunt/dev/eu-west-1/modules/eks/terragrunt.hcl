include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/eks.hcl"
  expose = true
}

inputs = {
  # Enter a list of IAM roles that should have access to the EKS cluster, the above will not work for your case its just an example
  kms_key_administrators = ["arn:aws:iam::326610803524:role/aws-reserved/sso.amazonaws.com/eu-west-1/AWSReservedSSO_AdministratorAccess_f0a07ce8bfe3e622"]

  on_demand_nodes = {
    ami_type       = "AL2_ARM_64" # "AL2_x86_64" for t3 and lower
    instance_types = ["t4g.small"]
    min_size       = 1
    max_size       = 5
    desired_size   = 0
    max_pods       = 110 # 96 for t3a.small, 34 for t3.micro and nano. 110 for anything above that
  }
  spot_nodes = {
    ami_type       = "AL2_ARM_64" # "AL2_x86_64" for t3 and lower
    instance_types = ["t4g.small"]
    min_size       = 2
    max_size       = 5
    desired_size   = 1
    max_pods       = 110 # 96 for t3a.small, 34 for t3.micro and nano. 110 for anything above that
  }
}
