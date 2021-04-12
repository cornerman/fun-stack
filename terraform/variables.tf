locals {
  region      = "eu-central-1"
  domain      = "funstack.net"
  domain_auth = "auth.${local.domain}"
  domain_ws   = "ws.${local.domain}"

  lambda_functions = {
    lambda-api = {
      handler     = "lambdaapi-opt.handler"
      filename    = "../build/lambda-api.zip"
      timeout     = 30
      memory_size = 256
      environment = {}
    }
    authorizer = {
      handler     = "index.handler"
      filename    = "authorizer/index.zip"
      timeout     = 30
      memory_size = 128
      environment = {
        COGNITO_POOL_ID = aws_cognito_user_pool.user.id
      }
    }
  }

  website_dir = "../web-client/target/scala-2.13/scalajs-bundler/main/prod"

  content_type_map = {
    html = "text/html",
    js   = "application/javascript",
    css  = "text/css",
    svg  = "image/svg+xml",
    jpg  = "image/jpeg",
    ico  = "image/x-icon",
    png  = "image/png",
    gif  = "image/gif",
    pdf  = "application/pdf"
  }
}
