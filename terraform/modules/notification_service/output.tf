output "lambda_function_name" {
  value = aws_lambda_function.notification_lambda.function_name
}

output "lambda_role_arn" {
  value = aws_iam_role.lambda_exec_role.arn
}
