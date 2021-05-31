module "fun" {
  source = "github.com/cornerman/fun-stack-tf?ref=master"

  domain               = "funstack.net"
  catch_all_forward_to = "johannes@karoff.net"

  website = {
    source_dir = "../web-client/target/scala-2.13/scalajs-bundler/main/dist/"
  }

  api = {
    source_dir            = "../lambda-api/target/scala-2.13/scalajs-bundler/main/"
    handler               = "lambdaapi-opt.handler"
    runtime               = "nodejs14.x"
    timeout               = 30
    memory_size           = 256
    allow_unauthenticated = true
  }

  auth = {
  }

  budget = {
    limit_monthly_dollar = "1.0"
    notify_email         = "johannes@karoff.net"
  }

  prod_workspace = "default"
  dev_workspaces = ["dev"]

  dev_setup = {
    local_website_url = "http://localhost:12345"
  }
}
