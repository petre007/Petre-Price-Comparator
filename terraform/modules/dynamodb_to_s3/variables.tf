variable "dynamodb_table_arn" {
  type        = string
  description = "ARN of the DynamoDB table"
}

variable "s3_bucket_arn" {
  type        = string
  description = "ARN of the target S3 bucket"
}
