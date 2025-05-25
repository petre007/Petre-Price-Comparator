output "dynamodb_table_arn" {
  description = "ARN of the DynamoDB table"
  value       = aws_dynamodb_table.this.arn
}

output "dynamodb_stream_arn" {
  description = "ARN of the DynamoDb stream table"
  value = aws_dynamodb_table.this.stream_arn
}