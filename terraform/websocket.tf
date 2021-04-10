resource "aws_apigatewayv2_api" "websocket" {
  name                       = "websocket"
  protocol_type              = "WEBSOCKET"
  route_selection_expression = "$request.body.action"
}

resource "aws_apigatewayv2_route" "websocket_default" {
  api_id    = aws_apigatewayv2_api.websocket.id
  route_key = "$default"

  target = "integrations/${aws_apigatewayv2_integration.websocket_default.id}"
}
resource "aws_apigatewayv2_route" "websocket_connect" {
  api_id    = aws_apigatewayv2_api.websocket.id
  route_key = "$connect"

  target = "integrations/${aws_apigatewayv2_integration.websocket_connect.id}"
}
resource "aws_apigatewayv2_route" "websocket_disconnect" {
  api_id    = aws_apigatewayv2_api.websocket.id
  route_key = "$disconnect"

  target = "integrations/${aws_apigatewayv2_integration.websocket_disconnect.id}"
}
resource "aws_apigatewayv2_integration" "websocket_default" {
  api_id                    = aws_apigatewayv2_api.websocket.id
  integration_type          = "AWS"
  credentials_arn           = aws_iam_role.websocket.arn
  content_handling_strategy = "CONVERT_TO_TEXT"
  integration_method        = "POST"
  integration_uri           = aws_lambda_function.lambda["lambda-api"].invoke_arn
  passthrough_behavior      = "WHEN_NO_MATCH"
}
resource "aws_apigatewayv2_integration" "websocket_connect" {
  api_id               = aws_apigatewayv2_api.websocket.id
  integration_type     = "AWS"
  credentials_arn      = aws_iam_role.websocket.arn
  integration_method   = "POST"
  integration_uri      = "arn:aws:apigateway:eu-central-1:dynamodb:action/PutItem"
  passthrough_behavior = "WHEN_NO_MATCH"
  timeout_milliseconds = 29000

  request_templates = {
    "application/json" = <<EOF
{
    "Item": {
      "connection_id": {
        "S": "$context.connectionId"
      },
      "request_id": {
        "S": "$$context.requestId"
      }
    },
    "TableName": "${aws_dynamodb_table.websocket_connections.name}"
}
EOF
  }
}
resource "aws_apigatewayv2_integration_response" "websocket_connect" {
  api_id                   = aws_apigatewayv2_api.websocket.id
  integration_id           = aws_apigatewayv2_integration.websocket_connect.id
  integration_response_key = "/200/"
}
resource "aws_apigatewayv2_route_response" "websocket_connect" {
  api_id             = aws_apigatewayv2_api.websocket.id
  route_id           = aws_apigatewayv2_route.websocket_connect.id
  route_response_key = "$default"
}
resource "aws_apigatewayv2_integration" "websocket_disconnect" {
  api_id               = aws_apigatewayv2_api.websocket.id
  integration_type     = "AWS"
  credentials_arn      = aws_iam_role.websocket.arn
  integration_method   = "POST"
  integration_uri      = "arn:aws:apigateway:eu-central-1:dynamodb:action/DeleteItem"
  passthrough_behavior = "WHEN_NO_MATCH"
  timeout_milliseconds = 29000

  request_templates = {
    "application/json" = <<EOF
{
    "Key": {
      "connection_id": {
        "S": "$context.connectionId"
      }
    },
    "TableName": "${aws_dynamodb_table.websocket_connections.name}"
}
EOF
  }
}
resource "aws_apigatewayv2_integration_response" "websocket_disconnect" {
  api_id                   = aws_apigatewayv2_api.websocket.id
  integration_id           = aws_apigatewayv2_integration.websocket_disconnect.id
  integration_response_key = "/200/"
}
resource "aws_apigatewayv2_route_response" "websocket_disconnect" {
  api_id             = aws_apigatewayv2_api.websocket.id
  route_id           = aws_apigatewayv2_route.websocket_disconnect.id
  route_response_key = "$default"
}
resource "aws_iam_role" "websocket" {
  name               = "websocket"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "apigateway.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "websocket" {
  role   = aws_iam_role.websocket.name
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "*"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_apigatewayv2_deployment" "websocket_default" {
  api_id      = aws_apigatewayv2_route.websocket_default.api_id

  lifecycle {
    create_before_destroy = true
  }
}
# resource "aws_apigatewayv2_deployment" "websocket_connect" {
#   api_id      = aws_apigatewayv2_route.websocket_connect.api_id

#   lifecycle {
#     create_before_destroy = true
#   }
# }
# resource "aws_apigatewayv2_deployment" "websocket_disconnect" {
#   api_id      = aws_apigatewayv2_route.websocket_disconnect.api_id

#   lifecycle {
#     create_before_destroy = true
#   }
# }

resource "aws_apigatewayv2_api_mapping" "websocket" {
  api_id      = aws_apigatewayv2_api.websocket.id
  domain_name = aws_apigatewayv2_domain_name.websocket.id
  stage       = aws_apigatewayv2_stage.websocket.id
}

resource "aws_apigatewayv2_stage" "websocket" {
  api_id = aws_apigatewayv2_api.websocket.id
  name   = "latest"

  default_route_settings {
    data_trace_enabled = true
    throttling_rate_limit = 100
    throttling_burst_limit = 50
  }
}

resource "aws_apigatewayv2_domain_name" "websocket" {
  domain_name = local.domain_ws

  domain_name_configuration {
    certificate_arn = aws_acm_certificate.ws.arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}
resource "aws_route53_record" "websocket" {
  name    = aws_apigatewayv2_domain_name.websocket.domain_name
  type    = "A"
  zone_id = data.aws_route53_zone.main.zone_id

  alias {
    name                   = aws_apigatewayv2_domain_name.websocket.domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.websocket.domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}

resource "aws_dynamodb_table" "websocket_connections" {
  name           = "websocket-connections"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "connection_id"
  range_key      = "request_id"

  attribute {
    name = "request_id"
    type = "S"
  }

  attribute {
    name = "connection_id"
    type = "S"
  }
}
