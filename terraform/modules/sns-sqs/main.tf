resource "aws_sns_topic" "topics" {
  for_each   = toset(var.topics)
  name       = "${each.key}"
  fifo_topic = false
}

resource "aws_sqs_queue" "queues" {
  for_each = toset(var.queues)

  name                         = "${each.key}"
  fifo_queue                   = false
}

# Flattened subscription map
locals {
  subscriptions_flat = {
    for pair in flatten([
      for topic, sqs_list in var.subscriptions : [
        for sqs in sqs_list : {
          key       = "${topic}_${sqs}"
          topic_arn = aws_sns_topic.topics[topic].arn
          queue_arn = aws_sqs_queue.queues[sqs].arn
          queue_url = aws_sqs_queue.queues[sqs].id
        }
      ]
    ]) : pair.key => {
      topic_arn = pair.topic_arn
      queue_arn = pair.queue_arn
      queue_url = pair.queue_url
    }
  }
}

# SNS to SQS subscriptions
resource "aws_sns_topic_subscription" "sqs_subscriptions" {
  for_each = local.subscriptions_flat

  topic_arn            = each.value.topic_arn
  protocol             = "sqs"
  endpoint             = each.value.queue_arn
  raw_message_delivery = true
}

# Allow SNS to send messages to the queues
resource "aws_sqs_queue_policy" "allow_sns" {
  for_each = local.subscriptions_flat

  queue_url = each.value.queue_url

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "AllowSNSPublish",
        Effect = "Allow",
        Principal = "*",
        Action = "sqs:SendMessage",
        Resource = each.value.queue_arn,
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = each.value.topic_arn
          }
        }
      }
    ]
  })
}

