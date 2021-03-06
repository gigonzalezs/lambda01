locals {
  pipeline_name = "${var.name}-${terraform.workspace}"
}


resource "aws_s3_bucket" "artifacts_bucket" {
  bucket_prefix = "${replace(local.pipeline_name, "_", "-")}-pipeline-"
  force_destroy = true
}


resource "aws_iam_role" "role" {
  name_prefix = "${local.pipeline_name}_codepipeline_"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "codepipeline.amazonaws.com",
          "events.amazonaws.com",
          "cloudformation.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "policy" {
  role = aws_iam_role.role.id

  policy = <<EOF
{
    "Statement": [
      {
            "Action": [
                "apigateway:*",
                "codedeploy:*",
                "lambda:*",
                "cloudformation:CreateChangeSet",
                "iam:GetRole",
                "iam:CreateRole",
                "iam:DeleteRole",
                "iam:PutRolePolicy",
                "iam:AttachRolePolicy",
                "iam:DeleteRolePolicy",
                "iam:DetachRolePolicy",
                "iam:PassRole"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Effect": "Allow",
            "Action": [
                "codepipeline:StartPipelineExecution"
            ],
            "Resource": "*"
        },
        {
            "Action": [
                "codecommit:CancelUploadArchive",
                "codecommit:GetBranch",
                "codecommit:GetCommit",
                "codecommit:GetUploadArchiveStatus",
                "codecommit:UploadArchive"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "codestar-connections:UseConnection"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "elasticbeanstalk:*",
                "ec2:*",
                "elasticloadbalancing:*",
                "autoscaling:*",
                "cloudwatch:*",
                "s3:*",
                "sns:*",
                "cloudformation:*",
                "rds:*",
                "sqs:*",
                "ecs:*"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "opsworks:CreateDeployment",
                "opsworks:DescribeApps",
                "opsworks:DescribeCommands",
                "opsworks:DescribeDeployments",
                "opsworks:DescribeInstances",
                "opsworks:DescribeStacks",
                "opsworks:UpdateApp",
                "opsworks:UpdateStack"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "cloudformation:CreateStack",
                "cloudformation:DeleteStack",
                "cloudformation:DescribeStacks",
                "cloudformation:UpdateStack",
                "cloudformation:CreateChangeSet",
                "cloudformation:DeleteChangeSet",
                "cloudformation:DescribeChangeSet",
                "cloudformation:ExecuteChangeSet",
                "cloudformation:SetStackPolicy",
                "cloudformation:ValidateTemplate"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "codebuild:BatchGetBuilds",
                "codebuild:StartBuild"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Effect": "Allow",
            "Action": [
                "devicefarm:ListProjects",
                "devicefarm:ListDevicePools",
                "devicefarm:GetRun",
                "devicefarm:GetUpload",
                "devicefarm:CreateUpload",
                "devicefarm:ScheduleRun"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "servicecatalog:ListProvisioningArtifacts",
                "servicecatalog:CreateProvisioningArtifact",
                "servicecatalog:DescribeProvisioningArtifact",
                "servicecatalog:DeleteProvisioningArtifact",
                "servicecatalog:UpdateProduct"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "cloudformation:ValidateTemplate"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "ecr:DescribeImages"
            ],
            "Resource": "*"
        }
    ],
    "Version": "2012-10-17"
}
EOF
}

resource "aws_codepipeline" "codepipeline" {
  name     = "${local.pipeline_name}_codepipeline"
  role_arn = aws_iam_role.role.arn

  artifact_store {
    location = aws_s3_bucket.artifacts_bucket.bucket
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      version          = "1"
      name             = "Source"
      category         = "Source"
      owner            = "AWS"
      provider         = "CodeCommit"
      output_artifacts = ["source"]

      configuration = {
        PollForSourceChanges = false
        RepositoryName       = var.repository_name
        BranchName           = var.repository_branch
      }
    }
  }


  stage {
    name = "Build"

    action {
      version         = "1"
      name            = "Build"
      category        = "Build"
      owner           = "AWS"
      provider        = "CodeBuild"
      input_artifacts = ["source"]
      output_artifacts = ["build_output"]

      configuration = {
        ProjectName = module.build.name
      }
    }
  }

  stage {
    name = "ContinuousDeploy"

    action {
      version         = "1"
      name            = "DeployOnQA"
      category        = "Deploy"
      owner           = "AWS"
      provider        = "CloudFormation"
      input_artifacts = ["build_output"]

      configuration = {
        ActionMode     = "REPLACE_ON_FAILURE"
        Capabilities   = "CAPABILITY_AUTO_EXPAND,CAPABILITY_IAM"
        RoleArn        =  aws_iam_role.role.arn,
        StackName      = "${var.name}-qa-${random_pet.stack_name.id}-stack",
        ChangeSetName  = "${var.name}-qa-${random_pet.stack_name.id}-changeSet",
        TemplatePath   = "build_output::qa-packaged-template.yml"
        ParameterOverrides = "{\"DatabaseURL\":\"${var.qa_db_url}\",\"DatabaseUser\":\"${var.qa_db_username}\",\"DatabasePassword\":\"${var.qa_db_password}\"}"
      }
    }
  }
   
  stage {
    name = "Accept"

    action {
      version         = "1"
      name            = "Approval"
      category        = "Approval"
      owner           = "AWS"
      provider        = "Manual"
    }
  }

  stage {
    name = "Deploy"

    action {
      version         = "1"
      name            = "DeploOnProd"
      category        = "Deploy"
      owner           = "AWS"
      provider        = "CloudFormation"
      input_artifacts = ["build_output"]

      configuration = {
        ActionMode     = "REPLACE_ON_FAILURE"
        Capabilities   = "CAPABILITY_AUTO_EXPAND,CAPABILITY_IAM"
        RoleArn        =  aws_iam_role.role.arn,
        StackName      = "${var.name}-prod-${random_pet.stack_name.id}-stack",
        ChangeSetName  = "${var.name}-prod-${random_pet.stack_name.id}-changeSet",
        TemplatePath   = "build_output::prod-packaged-template.yml"
        ParameterOverrides = "{\"DatabaseURL\":\"${var.prod_db_url}\",\"DatabaseUser\":\"${var.prod_db_username}\",\"DatabasePassword\":\"${var.prod_db_password}\"}"
      }
    }
  }
}

resource "random_pet" "stack_name" {}

data "aws_codecommit_repository" "codecommit" {
  repository_name = var.repository_name
}

resource "aws_cloudwatch_event_rule" "scm_change" {
  name_prefix = "${local.pipeline_name}_scm_change_"

  event_pattern = <<PATTERN
{
  "source": [ "aws.codecommit" ],
  "detail-type": [ "CodeCommit Repository State Change" ],
  "resources": [ "${data.aws_codecommit_repository.codecommit.arn}" ],
  "detail": {
    "event": [ "referenceCreated", "referenceUpdated" ],
    "referenceType": [ "branch" ],
    "referenceName": [ "${var.repository_branch}" ]
  }
}
PATTERN
}

resource "aws_cloudwatch_event_target" "scm_change_target" {
  rule     = aws_cloudwatch_event_rule.scm_change.name
  arn      = aws_codepipeline.codepipeline.arn
  role_arn = aws_iam_role.role.arn
}
