variable "name" {
  type = string
}

variable "domain" {
  type = string
}

variable "dev_mode" {
  type = object({
    local_website_url = string
    output_dir        = string
  })
  default = null
}

variable "auth" {
  type = object({
  })
}

variable "website" {
  type = object({
    source_dir = string
  })
}

variable "api" {
  type = object({
    source_dir  = string
    handler     = string
    timeout     = number
    memory_size = number
  })
}

locals {
  prefix = "${var.name}-${terraform.workspace}"

  domain         = terraform.workspace == "default" ? var.domain : "${terraform.workspace}.env.${var.domain}"
  domain_website = local.domain
  domain_auth    = "auth.${local.domain}"
  domain_ws      = "api.${local.domain}"
  redirect_urls = concat(
    ["https://${local.domain_website}"],
    var.dev_mode == null ? [] : [var.dev_mode.local_website_url]
  )

  api_zip_file        = "${path.module}/api.zip"
  authorizer_zip_file = "${path.module}/authorizer.zip"

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

  app_config = <<EOF
window.AppConfig = {
  "environment": "${terraform.workspace}",
  "domain": "${local.domain_website}",
  "domainAuth": "${local.domain_auth}",
  "domainWS": "${local.domain_ws}",
  "clientIdAuth": "${aws_cognito_user_pool_client.website_client.id}",
  "region": "${data.aws_region.current.name}",
  "identityPoolId": "${aws_cognito_identity_pool.user.id}",
  "cognitoEndpoint": "${aws_cognito_user_pool.user.endpoint}"
};
EOF
}
