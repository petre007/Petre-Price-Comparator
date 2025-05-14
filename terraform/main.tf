terraform {

    required_version = ">= 1.3"

    required_providers {
        aws = {
            source  = "hashicorp/aws"
            version = "~> 5.0"
        }
    }
}

provider "aws" {
    region = var.aws_region
}

module "cognito" {
  source          = "./modules/cognito"
  user_pool_name  = "price-comparator-user-pool"
}

module "ecr" {
  source           = "./modules/ecr"
  repository_name  = "price-comparator-backend"
  tags = {
    Environment = "dev"
    Project     = "price-comparator"
  }
}

module "ecs" {
  source = "./modules/ecs"

  clusters = [
    {
      name  = "dpc-auth"
      image = "123456789012.dkr.ecr.us-east-1.amazonaws.com/api:latest"
    },
  ]

  tags = {
    Environment = "dev"
    Project     = "price-comparator"
  }
}

# # 1. Generate random password
# resource "random_password" "rds_password" {
#   length  = 16
#   special = true
# }

# # 2. Create the secret
# resource "aws_secretsmanager_secret" "rds" {
#   name = "rds-credentials"
# }

# # 3. Store secret value (as JSON)
# resource "aws_secretsmanager_secret_version" "rds" {
#   secret_id     = aws_secretsmanager_secret.rds.id
#   secret_string = jsonencode({
#     username = "admin"
#     password = random_password.rds_password.result
#     dbname   = "price-catalog-db"
#   })
# }

module "rds" {
  source               = "./modules/rds"
  db_secret_arn        = "arn:aws:secretsmanager:eu-central-1:160885268864:secret:rds-credentials-abbuDm"
  db_instance_class    = "db.t4g.micro"
  db_engine            = "postgres"
  db_engine_version    = "17.2"
  allocated_storage    = 20
}