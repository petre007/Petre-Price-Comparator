variable "lambda_function_name" {
  type    = string
  default = "NotificationService"
}

variable "lambda_jar_path" {
  type        = string
  description = "Path to the local .jar file for the Lambda"
}

variable "lambda_handler" {
  type        = string
  description = "Java handler (e.g., com.example.Handler::handleRequest)"
}

variable "product_catalog_table_name" {
  type = string
}

variable "customers_table_name" {
  type = string
}

variable "stream_arn" {
  type = string
}
