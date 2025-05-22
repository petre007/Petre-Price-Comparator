output "region" {
  value = var.aws_region
}

output "rds_endpoint" {
  value = module.rds.rds_endpoint
}

output "table_arn" {
  value = module.dynamodb.dynamodb_table_arn
}