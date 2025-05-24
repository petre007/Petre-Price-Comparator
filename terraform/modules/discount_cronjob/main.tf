resource "aws_iam_role" "discount_cronjob_lambda_role" {
  name = "discount_cronjob_lambda_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_policy" "discount_cronjob_dynamodb_policy" {
  name = "DiscountCronjobDynamoDBAccess"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "dynamodb:GetItem",
        "dynamodb:Query",
        "dynamodb:Scan",
        "dynamodb:DescribeTable",
        "dynamodb:UpdateItem"
      ]
      Resource = var.dynamodb_table_arn
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.discount_cronjob_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "lambda_dynamodb_access" {
  role       = aws_iam_role.discount_cronjob_lambda_role.name
  policy_arn = aws_iam_policy.discount_cronjob_dynamodb_policy.arn
}


resource "aws_lambda_function" "discount_cronjob" {
  function_name = "DiscountCronjob"
  role          = aws_iam_role.discount_cronjob_lambda_role.arn
  handler       = "com.discountcronjob.DiscountCronjob::handleRequest"
  runtime       = "java11"
  memory_size   = 1024
  timeout       = 60

  filename         = var.lambda_zip_path
  source_code_hash = filebase64sha256(var.lambda_zip_path)
}

resource "aws_cloudwatch_event_rule" "discount_cronjob_schedule" {
  name                = "discount_cronjob_daily"
  schedule_expression = "rate(1 day)"
}

resource "aws_cloudwatch_event_target" "discount_cronjob_target" {
  rule      = aws_cloudwatch_event_rule.discount_cronjob_schedule.name
  target_id = "DiscountCronjobLambda"
  arn       = aws_lambda_function.discount_cronjob.arn
}

resource "aws_lambda_permission" "allow_eventbridge_invoke" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.discount_cronjob.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.discount_cronjob_schedule.arn
}
