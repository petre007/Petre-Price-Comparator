output "user_pool_id" {
  value = aws_cognito_user_pool.this.id
}

output "user_pool_client_id" {
  value = aws_cognito_user_pool_client.this.id
}

output "hosted_ui_url" {
  value = "https://${aws_cognito_user_pool_domain.this.domain}.auth.${var.region}.amazoncognito.com/login?client_id=${aws_cognito_user_pool_client.this.id}&response_type=code&scope=email+openid+profile&redirect_uri=${var.callback_urls[0]}"
}

output "user_pool_arn" {
  value = aws_cognito_user_pool.this.arn
}
