locals {
  domain      = "funstack.net"
  domain_auth = "auth.${local.domain}"

  lambda_functions = {
    lambda-api = {
      handler     = "lambdaapi-opt.handler"
      filename    = "../build/lambda-api.zip"
      timeout     = 30
      memory_size = 256
    }
  }

  website_dir = "../build/web-client/"

  content_type_map = {
    html     = "text/html",
    js       = "application/javascript",
    "js.map" = "application/javascript",
    css      = "text/css",
    svg      = "image/svg+xml",
    jpg      = "image/jpeg",
    ico      = "image/x-icon",
    png      = "image/png",
    gif      = "image/gif",
    pdf      = "application/pdf"
  }
}
