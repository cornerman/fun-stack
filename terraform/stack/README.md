# aws-app-tf

Create an opionated app environment on AWS.

This repository contains reusable terraform modules to build this app.

Also see client-library for coding usage:
- aws-app-scalajs

## What is it?

## How to use?

Copy example:

Go there and create an s3 bucket:
```
aws s3 mb s3://<my-terraform-state-bucket>
```

Edit terraform.tf and uncomment:
```
region = "<aws-region>"
bucket = "<my-terraform-state-bucket>"
```

Edit stack.tf and fill in the blanks.

Run:
```
terraform init
terraform apply
```

## Stages

You can have multiple environments, like dev,staging,prod.

## Modules

### Auth

Cognito user-pool.

### Websocket-Api

Authorized websocket with API Gateway.

### Website

Cloudfront, backed by s3 bucket.
