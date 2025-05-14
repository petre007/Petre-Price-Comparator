data "aws_secretsmanager_secret_version" "rds_creds" {
  secret_id = var.db_secret_arn
}

locals {
  db_creds = jsondecode(data.aws_secretsmanager_secret_version.rds_creds.secret_string)
}

resource "aws_db_instance" "this" {
  identifier             = local.db_creds.dbname
  engine                 = var.db_engine
  engine_version         = var.db_engine_version
  instance_class         = var.db_instance_class
  allocated_storage      = var.allocated_storage

  db_name                = local.db_creds.dbname
  username               = local.db_creds.username
  password               = local.db_creds.password

  skip_final_snapshot    = true
  deletion_protection    = false
  publicly_accessible    = true
  apply_immediately      = true

  tags = {
    Name = "rds-${local.db_creds.dbname}"
  }
}
