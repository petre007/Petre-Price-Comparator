resource "aws_cognito_user_pool" "this" {
  name = var.user_pool_name

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_uppercase = true
    require_numbers   = true
    require_symbols   = false
  }

  auto_verified_attributes = ["email"]
  mfa_configuration        = "OFF"

  schema {
    name     = "email"
    required = true
    attribute_data_type = "String"
    mutable  = true
  }

  schema {
    name     = "name"
    required = true
    attribute_data_type = "String"
    mutable  = true
  }

  schema {
    name     = "phone_number"
    required = true
    attribute_data_type = "String"
    mutable  = true
  }

  schema {
    name     = "address"
    required = true
    attribute_data_type = "String"
    mutable  = true
  }

  # You can add more optional fields if needed
  schema {
    name     = "birthdate"
    required = false
    attribute_data_type = "String"
    mutable  = true
  }

  schema {
    name     = "gender"
    required = false
    attribute_data_type = "String"
    mutable  = true
  }

  lambda_config {
    post_confirmation = var.post_confirmation_lambda_arn
  }
}

resource "aws_cognito_user_pool_client" "this" {
  name         = "${var.user_pool_name}-client"
  user_pool_id = aws_cognito_user_pool.this.id

  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]

  callback_urls = var.callback_urls
  logout_urls   = var.logout_urls

  allowed_oauth_flows = ["code"]
  allowed_oauth_scopes = ["email", "openid", "profile"]
  allowed_oauth_flows_user_pool_client = true

  supported_identity_providers = ["COGNITO"]
  generate_secret = false

  
}

resource "aws_cognito_user_pool_domain" "this" {
  domain       = var.domain_prefix
  user_pool_id = aws_cognito_user_pool.this.id
}
