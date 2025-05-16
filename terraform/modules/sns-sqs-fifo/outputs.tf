output "topic_arns" {
  value = {
    for k, topic in aws_sns_topic.topics : k => topic.arn
  }
}

output "queue_arns" {
  value = {
    for k, queue in aws_sqs_queue.queues : k => queue.arn
  }
}

output "queue_urls" {
  value = {
    for k, queue in aws_sqs_queue.queues : k => queue.id
  }
}
