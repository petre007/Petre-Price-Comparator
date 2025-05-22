variable "table_name" {
  description = "Name of the DynamoDB table"
  type        = string
}

variable "hash_key" {
  description = "Hash key for the DynamoDB table"
  type        = string
}

variable "range_key" {
  description = "Optional range key"
  type        = string
  default     = null
}

variable "attributes" {
  description = "List of attribute definitions"
  type = list(object({
    name = string
    type = string
  }))
}

variable "stream_view_type" {
  description = "Type of data to be written to the stream"
  type        = string
  default     = "NEW_AND_OLD_IMAGES"
}

variable "tags" {
  description = "Tags to apply to the table"
  type        = map(string)
  default     = {}
}
