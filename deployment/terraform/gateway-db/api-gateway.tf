## Mock gateway that responds with a userId and messageHash:
resource "aws_api_gateway_rest_api" "mock-gateway" {
  count       = var.deploy_mock_gateway_api ? 1 : 0 //only create if var.deploy_mock_gateway_api is true
  name        = "mock-gateway"
  description = "Mock gateway that returns the userId passed to it and messageHash"
  endpoint_configuration {
    types = ["REGIONAL"]
  }
  tags = merge(
    var.default_tags,
    { Name = "mock-gateway-api-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}

resource "aws_api_gateway_resource" "mock-gateway-resource" {
  count       = var.deploy_mock_gateway_api ? 1 : 0
  parent_id   = aws_api_gateway_rest_api.mock-gateway[count.index].root_resource_id
  path_part   = "mock-gateway"
  rest_api_id = aws_api_gateway_rest_api.mock-gateway[count.index].id
}

resource "aws_api_gateway_method" "mock-gateway-method" {
  count          = var.deploy_mock_gateway_api ? 1 : 0
  authorization  = "NONE"
  http_method    = "POST"
  resource_id    = aws_api_gateway_resource.mock-gateway-resource[count.index].id
  rest_api_id    = aws_api_gateway_rest_api.mock-gateway[count.index].id
  request_models = {
    "application/json" = aws_api_gateway_model.request-model[count.index].name
  }
}

resource "aws_api_gateway_model" "request-model" {
  count        = var.deploy_mock_gateway_api ? 1 : 0
  content_type = "application/json"
  name         = "MockGatewayRequestModel"
  rest_api_id  = aws_api_gateway_rest_api.mock-gateway[count.index].id
  schema       = jsonencode({
    "$schema" : "http://json-schema.org/draft-04/schema#",
    "title" : "MockGatewayRequestModel",
    "type" : "object",
    "properties" : {
      "userId" : {
        "type" : "string"
      },
      "workflowRunId" : {
        "type" : "string"
      }
    },
    "required" : [
      "userId",
      "workflowRunId"
    ]
  })
}

resource "aws_api_gateway_model" "response-model" {
  count        = var.deploy_mock_gateway_api ? 1 : 0
  content_type = "application/json"
  name         = "MockGatewayResponseModel"
  rest_api_id  = aws_api_gateway_rest_api.mock-gateway[count.index].id
  schema       = jsonencode({
    "$schema" : "http://json-schema.org/draft-04/schema#",
    "title" : "MockGatewayResponseModel",
    "type" : "object",
    "properties" : {
      "userId" : {
        "type" : "string"
      },
      "messageHash" : {
        "type" : "string"
      }
    },
    "required" : [
      "userId",
      "messageHash"
    ]
  })
}

resource "aws_api_gateway_integration" "mock-gateway-integration" {
  count             = var.deploy_mock_gateway_api ? 1 : 0
  http_method       = aws_api_gateway_method.mock-gateway-method[count.index].http_method
  resource_id       = aws_api_gateway_resource.mock-gateway-resource[count.index].id
  rest_api_id       = aws_api_gateway_rest_api.mock-gateway[count.index].id
  type              = "MOCK"
  request_templates = {
    "application/json" = "#set($context.requestOverride.path.body = $input.body){\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_method_response" "mock-gateway-method-response" {
  count       = var.deploy_mock_gateway_api ? 1 : 0
  http_method = aws_api_gateway_method.mock-gateway-method[count.index].http_method
  resource_id = aws_api_gateway_resource.mock-gateway-resource[count.index].id
  rest_api_id = aws_api_gateway_rest_api.mock-gateway[count.index].id
  status_code = 200

  response_models = {
    "application/json" = aws_api_gateway_model.response-model[count.index].name
  }

  depends_on = [aws_api_gateway_integration.mock-gateway-integration, aws_api_gateway_model.response-model]
}

resource "aws_api_gateway_integration_response" "mock-gateway-integration-response" {
  count       = var.deploy_mock_gateway_api ? 1 : 0
  http_method = aws_api_gateway_method.mock-gateway-method[count.index].http_method
  resource_id = aws_api_gateway_resource.mock-gateway-resource[count.index].id
  rest_api_id = aws_api_gateway_rest_api.mock-gateway[count.index].id
  status_code = "200"

  response_templates = {
    "application/json" = "#set($body = $context.requestOverride.path.body){\"userId\":\"$util.parseJson($body).userId\",\"messageHash\":\"test-hash\"}"
  }

  depends_on = [
    aws_api_gateway_integration.mock-gateway-integration,
    aws_api_gateway_method_response.mock-gateway-method-response,
    aws_api_gateway_rest_api.mock-gateway,
    aws_api_gateway_resource.mock-gateway-resource
  ]
}

resource "aws_api_gateway_deployment" "deployment" {
  count       = var.deploy_mock_gateway_api ? 1 : 0
  rest_api_id = aws_api_gateway_rest_api.mock-gateway[count.index].id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_rest_api.mock-gateway[count.index],
      aws_api_gateway_resource.mock-gateway-resource[count.index],
      aws_api_gateway_method.mock-gateway-method[count.index],
      aws_api_gateway_integration.mock-gateway-integration[count.index],
    ]))

  }
  variables = {
    deployed_at = timestamp()
  }

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [aws_api_gateway_rest_api.mock-gateway]
}

resource "aws_api_gateway_stage" "live-stage" {
  count         = var.deploy_mock_gateway_api ? 1 : 0
  deployment_id = aws_api_gateway_deployment.deployment[count.index].id
  rest_api_id   = aws_api_gateway_rest_api.mock-gateway[count.index].id
  stage_name    = "live"

  depends_on = [
    aws_api_gateway_rest_api.mock-gateway, aws_api_gateway_deployment.deployment, aws_api_gateway_account.global_logs
  ]
  tags = merge(
    var.default_tags,
    { Name = "mock-gateway-api-stage-${var.account_name}-${data.aws_caller_identity.current.account_id}-${var.region}" }
  )
}


resource "aws_api_gateway_method_settings" "mock-gateway-method-settings" {
  count       = var.deploy_mock_gateway_api ? 1 : 0
  method_path = "*/*"
  rest_api_id = aws_api_gateway_rest_api.mock-gateway[count.index].id
  stage_name  = aws_api_gateway_stage.live-stage[count.index].stage_name

  settings {
    logging_level   = "INFO"
    metrics_enabled = var.enable_mock_gateway_logs
  }

  depends_on = [
    aws_api_gateway_rest_api.mock-gateway,
    aws_api_gateway_stage.live-stage,
    aws_api_gateway_account.global_logs,
    aws_iam_role.global-cloudwatch-logs-api-gw,
  ]
}

resource "aws_api_gateway_account" "global_logs" {
  count               = var.configure_global_api_gateway_log_role ? 1 : 0
  cloudwatch_role_arn = aws_iam_role.global-cloudwatch-logs-api-gw[count.index].arn

  depends_on = [aws_iam_role_policy_attachment.allow_cloudwatch_logs_api_gw]
}

resource "aws_iam_role" "global-cloudwatch-logs-api-gw" {
  count              = var.configure_global_api_gateway_log_role ? 1 : 0
  name               = "global-cloudwatch-logs-api-gw"
  assume_role_policy = data.aws_iam_policy_document.assume_role_cloudwatch[count.index].json
  tags = merge(
    var.default_tags,
    { Name = "global-cloudwatch-logs-api-gw" }
  )
}

resource "aws_iam_role_policy_attachment" "allow_cloudwatch_logs_api_gw" {
  count      = var.configure_global_api_gateway_log_role ? 1 : 0
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
  role       = aws_iam_role.global-cloudwatch-logs-api-gw[count.index].name
}

data "aws_iam_policy_document" "assume_role_cloudwatch" {
  count   = var.configure_global_api_gateway_log_role ? 1 : 0
  version = "2012-10-17"
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["apigateway.amazonaws.com"]
    }
    effect = "Allow"
  }
}
