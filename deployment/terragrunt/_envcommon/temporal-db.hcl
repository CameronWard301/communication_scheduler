# Referenced from: https://github.com/gruntwork-io/terragrunt-infrastructure-live-example/blob/master/_envcommon/webserver-cluster.hcl
# ---------------------------------------------------------------------------------------------------------------------
# COMMON TERRAGRUNT CONFIGURATION
# This is the common component configuration for image ingestion. The common variables for each environment to
# deploy are defined here. This configuration will be merged into the environment configuration
# via an include block.
# ---------------------------------------------------------------------------------------------------------------------

# Terragrunt will copy the Terraform configurations specified by the source parameter, along with any files in the
# working directory, into a temporary folder, and execute your Terraform commands in that folder. If any environment
# needs to deploy a different module version, it should redefine this block with a different ref to override the
# deployed version.
terraform {
  #  source = "${local.base_source_url}?ref=v0.7.0"
  source = "${local.base_source_url}"
}

# ---------------------------------------------------------------------------------------------------------------------
# Locals are named constants that are reusable within the configuration.
# ---------------------------------------------------------------------------------------------------------------------
locals {
  # Automatically load environment-level variables
  environment_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))

  region_vars = read_terragrunt_config(find_in_parent_folders("region.hcl"))

  # Extract out common variables for reuse
  env = local.environment_vars.locals.environment

  # Expose the base source URL so different versions of the module can be deployed in different environments. This will
  # be used to construct the terraform block in the child terragrunt configurations.
  base_source_url = "../../../../../terraform//temporal-db"

  account_tags = read_terragrunt_config(find_in_parent_folders("default-tags.hcl"))
  env_tags     = {
    RepoURL = "https://github.com/CameronWard301/communication_scheduler/tree/master/deployment/terraform/temporal-db"
  }
}

dependency "networking" {
  config_path = "${get_terragrunt_dir()}/../networking"
/*  skip_outputs = true
  mock_outputs = {
    vpc_id = "vpc-07711fc9481557c6e"
    private_subnet_id_1 = "subnet-12345678"
    private_subnet_id_2 = "subnet-12345678"
  }*/
}


# ---------------------------------------------------------------------------------------------------------------------
# MODULE PARAMETERS
# These are the variables we have to pass in to use the module. This defines the parameters that are common across all
# environments.
# ---------------------------------------------------------------------------------------------------------------------
inputs = {
  account_name        = local.env
  region              = local.region_vars.locals.aws_region
  default_tags        = local.env_tags
  vpc_id              = dependency.networking.outputs.vpc_id
  private_subnet_id_1 = dependency.networking.outputs.private_subnet_1a_id
  private_subnet_id_2 = dependency.networking.outputs.private_subnet_1b_id

}
