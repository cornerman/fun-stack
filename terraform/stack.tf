module "fun" {
  source = "github.com/cornerman/fun-stack-tf"

  name = "fun"

  domain = "funstack.net"

  website = {
    source_dir = "../web-client/target/scala-2.13/scalajs-bundler/main/prod/"
  }

  api = {
    source_dir  = "../lambda-api/target/scala-2.13/scalajs-bundler/main/"
    handler     = "lambdaapi-opt.handler"
    runtime     = "nodejs14.x"
    timeout     = 30
    memory_size = 256
  }

  auth = {
  }

  allow_unauthenticated = true

  prod_workspace = "default"
  dev_workspaces = ["dev"]

  dev_setup = {
    local_website_url = "http://localhost:12345"
    config_output_dir = "../web-client/src/assetsDev/"
  }
}
