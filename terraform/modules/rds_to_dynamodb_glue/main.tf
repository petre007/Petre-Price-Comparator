resource "aws_iam_role" "glue_job_role" {
  name = "${var.name}_glue_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action    = "sts:AssumeRole",
        Principal = {
          Service = "glue.amazonaws.com"
        },
        Effect = "Allow",
        Sid     = ""
      }
    ]
  })
}

resource "aws_iam_role_policy" "glue_policy" {
  name = "${var.name}_glue_policy"
  role = aws_iam_role.glue_job_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "*"
      },
      {
        Effect = "Allow",
        Action = [
          "dynamodb:PutItem",
          "dynamodb:BatchWriteItem",
          "dynamodb:DescribeTable"
        ],
        Resource = var.dynamodb_table_arn
      },
      {
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ],
        Resource = var.secret_arn
      },
      {
        Effect = "Allow",
        Action = [
          "glue:GetConnection",
          "glue:GetConnections"
        ],
        Resource = "*"
      },
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ],
        Resource = [
          "arn:aws:s3:::aws-glue-assets-160885268864-eu-central-1",
          "arn:aws:s3:::aws-glue-assets-160885268864-eu-central-1/scripts",
          "arn:aws:s3:::aws-glue-assets-160885268864-eu-central-1/scripts/*"
        ]
      }
    ]
  })
}

resource "aws_glue_job" "this" {
  name     = var.name
  role_arn = aws_iam_role.glue_job_role.arn

  command {
    name            = "glueetl"
    script_location = var.script_location
    python_version  = "3"
  }

  glue_version = "4.0"
  number_of_workers = 2
  worker_type       = "G.1X"

  default_arguments = {
    "--job-language"                       = "python"
    "--enable-metrics"                     = "true"
    "--enable-continuous-cloudwatch-log"   = "true"
    "--job-bookmark-option"                = "job-bookmark-disable"
  }
}
