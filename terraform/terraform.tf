terraform {
  backend "s3" {
    encrypt = true
    region  = "eu-central-1"
    key     = "fun/my-app.tfstate"
    bucket  = "funstack-tfstate"
  }
}

provider "aws" {
  region = "eu-central-1"
}
