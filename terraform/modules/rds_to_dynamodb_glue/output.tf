output "glue_job_name" {
  value = aws_glue_job.this.name
}

output "iam_role_arn" {
  value = aws_iam_role.glue_job_role.arn
}
