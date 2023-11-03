include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/networking.hcl"
  expose = true
}

inputs = {
  cidr_block                   = "172.32.0.0/16"
  private_subnet_cidr_block_1a = "172.32.1.0/24"
  private_subnet_cidr_block_1b = "172.32.2.0/24"
}