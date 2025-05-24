variable "dynamodb_table_arn" {
  description = "ARN of the ProductCatalogDynamoDb table"
  type        = string
}

variable "lambda_zip_path" {
  description = "Path to the zipped Lambda deployment package"
  type        = string
}
