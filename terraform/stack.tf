module "fun" {
  source = "./stack/"

  domain = "funstack.net"

  website = {
    source_dir = "../web-client/target/scala-2.13/scalajs-bundler/main/prod/"
  }

  api = {
    source_dir  = "../lambda-api/target/scala-2.13/scalajs-bundler/main/"
    handler     = "lambdaapi-opt.handler"
    timeout     = 30
    memory_size = 256
  }

  auth = {
  }
}
