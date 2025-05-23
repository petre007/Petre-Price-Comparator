variable "name" {
  description = "The name of the Redis cluster"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for the subnet group"
  type        = list(string)
}

variable "security_group_ids" {
  description = "List of security group IDs to associate with the Redis cluster"
  type        = list(string)
}
