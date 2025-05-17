variable "user_pool_name" {
  description = "Name of the Cognito User Pool"
  type        = string
}

variable "callback_urls" {
  description = "Allowed OAuth callback URLs"
  type        = list(string)
  default     = ["http://localhost:3000/callback"]
}

variable "logout_urls" {
  description = "Allowed logout URLs"
  type        = list(string)
  default     = ["http://localhost:3000/logout"]
}

variable "domain_prefix" {
  description = "Prefix for Cognito Hosted UI domain (must be globally unique)"
  type        = string
  default     = "price-comparator-app"
}

variable "region" {
    description = "AWS region to deploy resources"
    type        = string
    default     = "eu-central-1"
}

variable "post_confirmation_lambda_arn" { 
  description = "RegisterLambda ARN"
  type = string
}