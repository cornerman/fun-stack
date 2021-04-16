resource "aws_cloudwatch_log_group" "lambda_authorizer" {
  name              = "/aws/lambda/authorizer"
  retention_in_days = 3
}

resource "aws_lambda_function" "authorizer" {
  function_name = "authorizer"
  role          = aws_iam_role.lambda_authorizer.arn

  timeout     = 30
  memory_size = 128
  publish     = true

  runtime          = "nodejs14.x"
  handler          = "index.handler"
  filename         = "${path.module}/authorizer/authorizer.zip"
  source_code_hash = filebase64sha256("${path.module}/authorizer/authorizer.zip")

  environment {
    variables = {
      COGNITO_POOL_ID = aws_cognito_user_pool.user.id
    }
  }
}

resource "aws_iam_role" "lambda_authorizer" {
  name               = "lambda-authorizer"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "lambda_authorizer" {
  role       = aws_iam_role.lambda_authorizer.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
