variable "name" {
  type = string
}

variable "domain" {
  type = string
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
  prefix = "${terraform.workspace}-${var.name}"

  domain         = terraform.workspace == "default" ? var.domain : "${terraform.workspace}.env.${var.domain}"
  domain_website = local.domain
  domain_auth    = "auth.${local.domain}"
  domain_ws      = "api.${local.domain}"

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
}
