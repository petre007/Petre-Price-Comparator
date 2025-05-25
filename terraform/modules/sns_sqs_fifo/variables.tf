variable "name" {
  description = "Base name for SNS topic and SQS queue (without .fifo)"
  type        = string
}

variable "enable_content_based_deduplication" {
  description = "Whether to enable content-based deduplication"
  type        = bool
  default     = true
}

variable "message_retention_seconds" {
  description = "How long messages are retained"
  type        = number
  default     = 345600 # 4 days
}

variable "visibility_timeout_seconds" {
  description = "Visibility timeout for SQS messages"
  type        = number
  default     = 30
}

variable "receive_wait_time_seconds" {
  description = "Wait time for long polling"
  type        = number
  default     = 10
}
