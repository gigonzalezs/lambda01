locals {
  bucket_key                  = "wabipay_function/${terraform.workspace}/wabipay_function.zip"
  wabi_pay_to_wabilytics_name = "${terraform.workspace}-wabiPayToWabilytics"
}

data "aws_s3_bucket_object" "lambda_artifact" {
  bucket = data.terraform_remote_state.repositories.outputs.artifacts_bucket
  key    = local.bucket_key
}

resource "aws_iam_role" "lambda_exec" {
  name = "${terraform.workspace}-iam_lambda_wabipay"

  assume_role_policy = <<EOF
{
   "Version": "2012-10-17",
   "Statement": [
     {
       "Action": "sts:AssumeRole",
       "Principal": {
         "Service": "lambda.amazonaws.com"
       },
       "Effect": "Allow"
     }
   ]
}
EOF
}

resource "aws_lambda_function" "wabi_pay_to_wabilytics_function" {
  s3_bucket        = data.terraform_remote_state.repositories.outputs.artifacts_bucket
  s3_key           = local.bucket_key
  source_code_hash = data.aws_s3_bucket_object.lambda_artifact.version_id
  function_name    = local.wabi_pay_to_wabilytics_name
  handler          = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  description      = "wabipay processor for wabilytics"
  memory_size      = "256"
  runtime          = "java8"
  timeout          = "60"
  dead_letter_config {
    target_arn = aws_sqs_queue.lambda_dead_letter.arn
  }
  role = aws_iam_role.lambda_exec.arn
  vpc_config {
    subnet_ids = data.terraform_remote_state.foundation.outputs.private_subnets
    security_group_ids = [
      data.terraform_remote_state.foundation.outputs.rds_security_group,
      aws_security_group.lambda_security_group.id
    ]
  }
  environment {
    variables = {
      MAIN_CLASS               = "wabilytics.wabipay.Configuration"
      FUNCTION_NAME            = "wabiPayToWabilytics"
      LOGGING_LEVEL_WABILYTICS = "DEBUG"
      DB_URL                   = "jdbc:mysql://${data.terraform_remote_state.foundation.outputs.rds_endpoint}/${data.terraform_remote_state.foundation.outputs.rds_name}?useUnicode=yes&characterEncoding=UTF-8"
      DB_USERNAME              = data.terraform_remote_state.foundation.outputs.rds_user_name
      DB_PASSWORD              = data.aws_secretsmanager_secret_version.rds_password.secret_string
      DB_MAXPOOLSIZE           = local.graphql.db_pool_size[terraform.workspace]
      #miliseconds
      HTTP_CLIENT_CONNECTION_TIMEOUT = local.graphql.http.connection_timeout[terraform.workspace]
      #miliseconds
      HTTP_CLIENT_READ_TIMEOUT = local.graphql.http.read_timeout[terraform.workspace]
      GRAPHQL_URL              = local.graphql.url[terraform.workspace]
      GRAPHQL_CLIENT           = local.graphql.user[terraform.workspace]
      GRAPHQL_CLIENT_SECRET    = data.aws_secretsmanager_secret_version.graphql_client_secret.secret_string
    }
  }
  tags = {
    environment = terraform.workspace
  }
}

resource "aws_cloudwatch_log_group" "wabi_pay_to_wabilytics_function_log" {
  name              = "/aws/lambda/${local.wabi_pay_to_wabilytics_name}"
  retention_in_days = "7"
}

resource "aws_iam_policy" "lambda_logging" {
  name        = "${terraform.workspace}-lambda_wabipay_logging"
  path        = "/"
  description = "IAM policy for logging from a lambda"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = aws_iam_policy.lambda_logging.arn
}

resource "aws_iam_policy" "lambda_to_sqs" {
  name        = "${terraform.workspace}-lambda_wabipay_to_sqs"
  description = "IAM policy for send messages to sqs"
  policy      = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "sqs:SendMessage"
      ],
      "Resource": "${aws_sqs_queue.lambda_dead_letter.arn}",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "lambda_sqs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = aws_iam_policy.lambda_to_sqs.arn
}

resource "aws_lambda_event_source_mapping" "wabipay_to_lamba" {
  event_source_arn = aws_sqs_queue.wabipay_events.arn
  function_name    = aws_lambda_function.wabi_pay_to_wabilytics_function.arn
}

resource "aws_iam_policy_attachment" "lambda-sqs-access" {
  name       = "${terraform.workspace}-lambda-sqs-access"
  policy_arn = aws_iam_policy.lambda-sqs-access.arn
  roles      = [aws_iam_role.lambda_exec.name]
}

resource "aws_iam_role_policy_attachment" "lambda_attach_vpc_access_execution_role" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "lambda_attach_basic_execution_role" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_security_group" "lambda_security_group" {
  name_prefix = "${var.name}_${terraform.workspace}_lambda_"
  vpc_id      = data.terraform_remote_state.foundation.outputs.vpc

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    environment = terraform.workspace
  }
}
