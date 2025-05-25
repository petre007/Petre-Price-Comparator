output "sns_topic_arn" {
  value = aws_sns_topic.fifo_topic.arn
}

output "sqs_queue_arn" {
  value = aws_sqs_queue.fifo_queue.arn
}

output "sqs_queue_url" {
  value = aws_sqs_queue.fifo_queue.id
}
