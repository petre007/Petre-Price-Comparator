output "region" {
  value = var.aws_region
}

output "rds_endpoint" {
  value = module.rds.rds_endpoint
}