resource "aws_iam_role" "glue_role" {
  name = "dynamodb_to_s3_glue_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "glue.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "glue_policy" {
  name = "dynamodb_to_s3_glue_policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
            "dynamodb:BatchGetItem",
            "dynamodb:GetItem",
            "dynamodb:Scan",
            "dynamodb:Query",
            "dynamodb:DescribeTable"
        ],
        Resource = var.dynamodb_table_arn
      },
      {
        Effect = "Allow",
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:ListBucket"
        ],
        Resource = [
          var.s3_bucket_arn,
          "${var.s3_bucket_arn}/*"
        ]
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
      },
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
          "glue:GetTable",
          "glue:GetTables",
          "glue:GetDatabase",
          "glue:GetDatabases"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_glue_policy" {
  role       = aws_iam_role.glue_role.name
  policy_arn = aws_iam_policy.glue_policy.arn
}

resource "aws_glue_job" "dynamodb_to_s3_etl" {
  name     = "dynamodb-to-s3-etl"
  role_arn = aws_iam_role.glue_role.arn

  command {
    name            = "glueetl"
    script_location = "s3://aws-glue-assets-160885268864-eu-central-1/scripts/dynamodb_to_s3.py"
    python_version  = "3"
  }

  glue_version       = "4.0"
  number_of_workers  = 2
  worker_type        = "G.1X"
  max_retries        = 0
}

output "glue_job_name" {
  value = aws_glue_job.dynamodb_to_s3_etl.name
}
