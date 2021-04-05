resource "aws_cognito_user_pool" "user" {
  name = "fun-user"

  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }
  password_policy {
    temporary_password_validity_days = 7
    minimum_length    = 6
    require_lowercase = false
    require_numbers   = false
    require_symbols   = false
    require_uppercase = false
  }
}
resource "aws_cognito_user_pool_client" "client" {
  name         = "fun-client"
  user_pool_id = aws_cognito_user_pool.user.id

  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_flows = [
    "code",
    "implicit",
  ]
  allowed_oauth_scopes = [
    "aws.cognito.signin.user.admin",
    "email",
    "openid",
    "phone",
    "profile",
  ]
  explicit_auth_flows = [
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_PASSWORD_AUTH",
  ]
  supported_identity_providers = [
    "COGNITO",
  ]
  logout_urls = [
    "https://${local.domain}",
  ]
  callback_urls = [
    "https://${local.domain}",
  ]
}

resource "aws_cognito_identity_pool" "user" {
  identity_pool_name               = "fun_user"

  allow_unauthenticated_identities = false

  cognito_identity_providers {
    client_id               = aws_cognito_user_pool_client.client.id
    provider_name           = aws_cognito_user_pool.user.endpoint
    server_side_token_check = false
  }
}

resource "aws_cognito_user_pool_domain" "user" {
  domain          = local.domain_auth
  user_pool_id    = aws_cognito_user_pool.user.id
  certificate_arn = aws_acm_certificate.auth.arn

  depends_on = [aws_route53_record.website]
}

resource "aws_route53_record" "cognito" {
  name    = aws_cognito_user_pool_domain.user.domain
  type    = "A"
  zone_id = data.aws_route53_zone.main.zone_id
  alias {
    evaluate_target_health = false
    name                   = aws_cognito_user_pool_domain.user.cloudfront_distribution_arn
    zone_id                = "Z2FDTNDATAQYW2" # This zone_id is fixed
  }
}

resource "aws_iam_role" "user" {
  name = "identity-user"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "cognito-identity.amazonaws.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "cognito-identity.amazonaws.com:aud": "${aws_cognito_identity_pool.user.id}"
        },
        "ForAnyValue:StringLike": {
          "cognito-identity.amazonaws.com:amr": "authenticated"
        }
      }
    }
  ]
}
EOF
}

resource "aws_iam_policy" "user" {
  name        = "identity-user"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "lambda:InvokeFunction"
            ],
            "Resource": ${jsonencode([ for lambda in aws_lambda_function.lambda: lambda.arn])}
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "user" {
  role       = aws_iam_role.user.name
  policy_arn = aws_iam_policy.user.arn
}

resource "aws_cognito_identity_pool_roles_attachment" "user" {
  identity_pool_id = aws_cognito_identity_pool.user.id

  roles = {
    "authenticated" = aws_iam_role.user.arn
  }
}
