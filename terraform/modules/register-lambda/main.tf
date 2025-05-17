resource "aws_lambda_function" "this" {
  function_name = "RegisterLambda"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "com.register.RegisterLambda::handleRequest"
  runtime       = "java11"

  filename         = var.jar_path
  source_code_hash = filebase64sha256(var.jar_path)

  memory_size = 1024
  timeout     = 15           

  environment {
    variables = {
      SNS_TOPIC_ARN = var.sns_topic_arn
    }
  }
}

resource "aws_iam_role" "lambda_exec" {
  name = "RegisterLambda-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Principal = {
        Service = "lambda.amazonaws.com"
      },
      Effect = "Allow",
      Sid    = ""
    }]
  })
}

resource "aws_iam_role_policy" "lambda_sns_publish" {
  name = "RegisterLambdaSNSPublish"
  role = aws_iam_role.lambda_exec.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sns:Publish"
        ]
        Resource = var.sns_topic_arn  # ARN of register_topic
      },
      {
        Effect = "Allow"
        Action = [
          "lambda:InvokeFunction"
        ]
        Resource = var.user_pool_arn  # ARN of register_topic
      }
    ]
  })
}

resource "aws_lambda_permission" "allow_cognito" {
  statement_id  = "AllowCognitoInvoke"
  action        = "lambda:InvokeFunction"
  function_name = "RegisterLambda"
  principal     = "cognito-idp.amazonaws.com"
  source_arn    = var.user_pool_arn
}

resource "aws_iam_role_policy_attachment" "basic" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_sns_topic_subscription" "lambda" {
  topic_arn = var.sns_topic_arn
  protocol  = "lambda"
  endpoint  = aws_lambda_function.this.arn
}

resource "aws_lambda_permission" "sns" {
  statement_id  = "AllowExecutionFromSNS"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.this.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = var.sns_topic_arn
}
