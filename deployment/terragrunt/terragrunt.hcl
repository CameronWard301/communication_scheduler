# Adapted from example: https://github.com/gruntwork-io/terragrunt-infrastructure-live-example/blob/master/terragrunt.hcl
# ---------------------------------------------------------------------------------------------------------------------
# TERRAGRUNT CONFIGURATION
# Terragrunt is a thin wrapper for Terraform that provides extra tools for working with multiple Terraform modules,
# remote state, and locking: https://github.com/gruntwork-io/terragrunt
# ---------------------------------------------------------------------------------------------------------------------

locals {
  # Automatically load account-level variables
  account_vars = read_terragrunt_config(find_in_parent_folders("account.hcl"))

  # Automatically load region-level variables
  region_vars = read_terragrunt_config(find_in_parent_folders("region.hcl"))

  # Automatically load environment-level variables
  environment_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))

  tags_file = read_terragrunt_config(find_in_parent_folders("default-tags.hcl"))

  # Extract the variables we need for easy access
  account_name = local.account_vars.locals.account_name
  account_id   = local.account_vars.locals.aws_account_id
  aws_region   = local.region_vars.locals.aws_region
  tags         = local.tags_file.locals.default-tags

}

# Generate an AWS provider block
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents  = <<EOF
  provider "aws" {
  region = "${local.aws_region}"
  # uncomment here for local testing
  shared_credentials_files = ["~/.aws/credentials"]
  profile = "saml"

  default_tags {
  # Use heredoc syntax to render the json to avoid quoting complications.
      tags = jsondecode(<<INNEREOF
  ${local.tags}
  INNEREOF
  )
    }

  # Only these AWS Account IDs may be operated on by this template
  allowed_account_ids = ["${local.account_id}"]

}
EOF
}

# Configure Terragrunt to automatically store tfstate files in an S3 bucket
remote_state {
  backend = "s3"
  config = {
    encrypt        = true
    # uncomment the rest of the lines in the config for local testing:
    profile        = "saml"
    bucket         = "terraform-state-${local.account_name}-${local.account_id}-${local.aws_region}"
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.aws_region
    dynamodb_table = "terraform-locks"
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}

# ---------------------------------------------------------------------------------------------------------------------
# GLOBAL PARAMETERS
# These variables apply to all configurations in this subfolder. These are automatically merged into the child
# `terragrunt.hcl` config via the include block.
# ---------------------------------------------------------------------------------------------------------------------

# Configure root level variables that all resources can inherit. This is especially helpful with multi-account configs
# where terraform_remote_state data sources are placed directly into the modules.
inputs = merge(
  local.account_vars.locals,
  local.region_vars.locals,
  local.environment_vars.locals
)
