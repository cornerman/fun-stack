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
  domain_website = var.domain
  domain_auth    = "auth.${var.domain}"
  domain_ws      = "api.${var.domain}"

  api_zip_file = "api.zip"

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
