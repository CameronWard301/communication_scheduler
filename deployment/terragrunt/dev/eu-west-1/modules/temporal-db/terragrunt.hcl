include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/temporal-db.hcl"
  expose = true
}


inputs = {
  sg_eks_db_cidr = "172.32.0.0/16" // anywhere within the VPC
  engine_version = "13.12"
}
