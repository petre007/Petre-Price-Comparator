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