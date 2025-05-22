variable "name" {
  description = "Glue job name"
  type        = string
}

variable "script_location" {
  description = "Location of the Glue job script (S3 path is still required by Glue)"
  type        = string
}

variable "dynamodb_table_arn" {
  description = "ARN of the DynamoDB table"
  type        = string
}

variable "secret_arn" {
  description = "ARN of the Secrets Manager secret"
  type        = string
}
