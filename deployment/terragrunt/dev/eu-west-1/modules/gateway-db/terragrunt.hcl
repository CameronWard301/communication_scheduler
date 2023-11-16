include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/gateway-db.hcl"
  expose = true
}

inputs = {
  billing_mode = "PAY_PER_REQUEST"
}