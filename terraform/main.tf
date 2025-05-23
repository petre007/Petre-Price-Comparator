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
  post_confirmation_lambda_arn = module.register_lambda.lambda_arn
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
      image = null
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
  source = "./modules/sns-sqs"

  topics = ["register_topic"]
  queues = ["register_sqs"]

  subscriptions = {
    register_topic = ["register_sqs"]
  }
}

module "register_lambda" {
  source        = "./modules/register-lambda"
  jar_path      = var.jar_path
  sns_topic_arn = module.sns_sqs_fifo.topic_arns["register_topic"]
  user_pool_arn   = module.cognito.user_pool_arn
}

module "dynamodb" {
  source         = "./modules/dynamodb"
  table_name     = "PriceCatalogDynamoDb"
  hash_key       = "product_id"
  range_key      = "shop"

  attributes = [
    { name = "product_id", type = "N" },
    { name = "shop", type = "S" },
  ]

  tags = {
    Environment = "dev"
    Project     = "FreeTierDynamo"
  }
}

module "glue_rds_to_dynamodb_migration_job" {
  source = "./modules/rds_to_dynamodb_glue"

  name              = "migrate-rds-to-dynamo"
  script_location   = "s3://aws-glue-assets-160885268864-eu-central-1/scripts/"
  dynamodb_table_arn = module.dynamodb.dynamodb_table_arn
  secret_arn         = "arn:aws:secretsmanager:eu-central-1:160885268864:secret:rds-credentials-abbuDm"
}

module "redis" {
  source            = "./modules/redis"
  name              = "redis-price-comparator"
  subnet_ids        = ["subnet-0e081079b1591d50f", "subnet-0d451fa3dcf350ae9", "subnet-057244abd18335e11"]
  security_group_ids = ["sg-0569935f80ebd47ee"]
}

module "update_discount_lambda" {
  source           = "./modules/apply_discount_lambda"
  lambda_name      = "ApplyDiscount"
  lambda_handler   = "com.discount.ApplyDiscountHandler::handleRequest"
  lambda_jar_path  = "../java/serverless/ApplyDiscount/target/ApplyDiscount-1.0-SNAPSHOT.jar"
  dynamodb_table_arn = module.dynamodb.dynamodb_table_arn
}