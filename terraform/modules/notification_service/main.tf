data "aws_region" "current" {}
data "aws_caller_identity" "current" {}

resource "aws_iam_role" "lambda_exec_role" {
  name = "${var.lambda_function_name}_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_policy" "lambda_policy" {
  name = "${var.lambda_function_name}_policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid    = "DynamoDBAccess"
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "dynamodb:DescribeTable"
        ]
        Resource = [
            "arn:aws:dynamodb:*:*:table/${var.product_catalog_table_name}",
            "arn:aws:dynamodb:*:*:table/${var.customers_table_name}",
            "arn:aws:dynamodb:*:*:table/${var.customers_table_name}/index/product_id-index"
        ]
      },
      {
        Sid    = "StreamAccess"
        Effect = "Allow"
        Action = [
          "dynamodb:DescribeStream",
          "dynamodb:GetRecords",
          "dynamodb:GetShardIterator",
          "dynamodb:ListStreams"
        ]
        Resource = "*"
      },
      {
        Sid    = "SNSPublish"
        Effect = "Allow"
        Action = [
          "sns:Publish"
        ]
        Resource = "*"
      },
      {
        Sid    = "CloudWatchLogs"
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_attachment" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = aws_iam_policy.lambda_policy.arn
}

resource "aws_lambda_function" "notification_lambda" {
  function_name = var.lambda_function_name
  role          = aws_iam_role.lambda_exec_role.arn
  handler       = var.lambda_handler
  runtime       = "java11"
  filename      = var.lambda_jar_path
  memory_size = 1024
  timeout = 30

  environment {
    variables = {
      CUSTOMERS_TABLE_NAME       = var.customers_table_name
      PRODUCT_CATALOG_TABLE_NAME = var.product_catalog_table_name
    }
  }
}

resource "aws_lambda_event_source_mapping" "dynamodb_trigger" {
  event_source_arn = var.stream_arn
  function_name    = aws_lambda_function.notification_lambda.arn
  starting_position = "LATEST"
  enabled           = true
}

resource "aws_sns_sms_preferences" "sms_prefs" {
  default_sender_id = "Notifier"
  default_sms_type  = "Transactional"
}
