locals {
  wabipay_account = "arn:aws:iam::918079979680:root"
}

resource "aws_sqs_queue" "lambda_dead_letter" {
  name_prefix = "${terraform.workspace}-wabipayLambda-DeadLetter-"
  tags = {
    environment = terraform.workspace
  }
}

resource "aws_sqs_queue" "wabipay_events" {
  name_prefix                = "${terraform.workspace}-wabipay-events-"
  visibility_timeout_seconds = 120
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.lambda_dead_letter.arn
    maxReceiveCount     = 5
  })
  tags = {
    environment = terraform.workspace
  }
}

resource "aws_iam_policy" "lambda-sqs-access" {
  name   = "${terraform.workspace}-lambda-sqs-access"
  policy = <<EOF
{
   "Version": "2012-10-17",
   "Statement": [{
      "Effect": "Allow",
      "Action": "sqs:*",
      "Resource": "${aws_sqs_queue.wabipay_events.arn}"
   }]
}
EOF
}

resource "aws_sqs_queue_policy" "wabipay_events_sqs_policy" {
  queue_url = aws_sqs_queue.wabipay_events.id

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Id": "sqspolicy",
  "Statement": [
    {
      "Sid": "First",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:*",
      "Resource": "${aws_sqs_queue.wabipay_events.arn}"
    }
  ]
}
POLICY
}
