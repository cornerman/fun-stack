terraform {
  backend "s3" {
    encrypt = true
    region  = "eu-central-1"
    key     = "fun/my-app.tfstate"
    bucket  = "funstack-tfstate"
  }

  required_version = "0.14.7"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.36.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
}
