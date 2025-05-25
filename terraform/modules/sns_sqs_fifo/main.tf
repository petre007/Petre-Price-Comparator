# sqs_sns_fifo/main.tf
resource "aws_sns_topic" "fifo_topic" {
  name                        = "${var.name}.fifo"
  fifo_topic                  = true
  content_based_deduplication = var.enable_content_based_deduplication
}

resource "aws_sqs_queue" "fifo_queue" {
  name                              = "${var.name}.fifo"
  fifo_queue                        = true
  content_based_deduplication       = var.enable_content_based_deduplication
  message_retention_seconds         = var.message_retention_seconds
  visibility_timeout_seconds        = var.visibility_timeout_seconds
  receive_wait_time_seconds         = var.receive_wait_time_seconds
}

resource "aws_sqs_queue_policy" "sqs_policy" {
  queue_url = aws_sqs_queue.fifo_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "Allow-SNS-SendMessage"
        Effect    = "Allow"
        Principal = {
          Service = "sns.amazonaws.com"
        }
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.fifo_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.fifo_topic.arn
          }
        }
      }
    ]
  })
}

resource "aws_sns_topic_subscription" "sns_to_sqs" {
  topic_arn = aws_sns_topic.fifo_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.fifo_queue.arn
  raw_message_delivery = true
}
