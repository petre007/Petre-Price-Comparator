variable "jar_path" {
  description = "Path to the Lambda JAR file"
  type        = string
}

variable "sns_topic_arn" {
  description = "ARN of the SNS topic to publish messages to"
  type        = string
}

variable "user_pool_arn" {
  type = string
}