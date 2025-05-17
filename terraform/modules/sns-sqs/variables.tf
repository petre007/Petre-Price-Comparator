variable "topics" {
  description = "List of FIFO SNS topic names (no .fifo)"
  type        = list(string)
}

variable "queues" {
  description = "List of FIFO SQS queue names (no .fifo)"
  type        = list(string)
}

variable "subscriptions" {
  description = "Map of SNS topic name to list of SQS queue names"
  type        = map(list(string))
}