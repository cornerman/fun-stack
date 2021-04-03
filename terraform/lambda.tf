resource "aws_cloudwatch_log_group" "lambda" {
  for_each = local.lambda_functions

  name              = "/lambda/${each.key}"
  retention_in_days = 3
}

resource "aws_lambda_function" "lambda" {
  for_each = local.lambda_functions

  function_name    = each.key
  role             = aws_iam_role.lambda[each.key].arn
  timeout          = each.value.timeout
  memory_size      = each.value.memory_size
  filename         = each.value.filename
  handler          = each.value.handler
  runtime          = "nodejs12.x"
  publish          = true
  source_code_hash = filebase64sha256(each.value.filename)
}

resource "aws_iam_role" "lambda" {
  for_each = local.lambda_functions

  name               = "lambda-${each.key}"
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

resource "aws_iam_role_policy_attachment" "lambda_exec" {
  for_each = local.lambda_functions

  role       = aws_iam_role.lambda[each.key].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
