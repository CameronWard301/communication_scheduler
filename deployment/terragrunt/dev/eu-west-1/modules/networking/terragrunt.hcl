include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/networking.hcl"
  expose = true
}

inputs = {
  cidr_block = "172.32.0.0/16"
}