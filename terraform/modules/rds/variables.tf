variable "db_secret_arn" {
  type        = string
  description = "ARN of the Secrets Manager secret with DB credentials"
}

variable "db_instance_class" {
  type        = string
  description = "RDS instance type"
}

variable "db_engine" {
  type        = string
  description = "Database engine"
}

variable "db_engine_version" {
  type        = string
  description = "Engine version"
}

variable "allocated_storage" {
  type        = number
  description = "Storage in GB"
}
