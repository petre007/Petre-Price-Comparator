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

module "rds" {
  source               = "./modules/rds"
  db_secret_arn        = "arn:aws:secretsmanager:eu-central-1:160885268864:secret:rds-credentials-abbuDm"
  db_instance_class    = "db.t4g.micro"
  db_engine            = "postgres"
  db_engine_version    = "17.2"
  allocated_storage    = 20
}

module "s3" {
  source = "./modules/s3"

  bucket_name        = "petre-price-comparator-12345"
  versioning_enabled = true
  force_destroy      = false
  enable_sse         = true
  tags = {
    Environment = "dev"
    Owner       = "your-name"
  }
}

module "sns_sqs_fifo" {
  source = "./modules/sns-sqs-fifo"

  topics = ["register_topic"]
  queues = ["register_sqs"]

  subscriptions = {
    register_topic = ["register_sqs"]
  }
}
