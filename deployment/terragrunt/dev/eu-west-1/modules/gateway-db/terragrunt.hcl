include "root" {
  path = find_in_parent_folders()
}

include "envcommon" {
  path   = "${dirname(find_in_parent_folders())}/_envcommon/gateway-db.hcl"
  expose = true
}

inputs = {
  billing_mode                          = "PAY_PER_REQUEST",
  deploy_mock_gateway_api               = true
  configure_global_api_gateway_log_role = true
  enable_mock_gateway_logs              = true
}
