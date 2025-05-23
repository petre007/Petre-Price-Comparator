variable "lambda_name" {
  description = "Name of the Lambda function"
  type        = string
}

variable "lambda_handler" {
  description = "Java handler (e.g., example.UpdateDiscountHandler)"
  type        = string
}

variable "lambda_jar_path" {
  description = "Path to the compiled JAR file"
  type        = string
}

variable "dynamodb_table_arn" {
  description = "ARN of the DynamoDB table"
  type        = string
}
