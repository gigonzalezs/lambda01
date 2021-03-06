
data "template_file" "buildspec" {
  template = file("${path.module}/templates/buildspec.yml")
  vars = {
    qa_template = "${base64encode(data.template_file.qa_sam_template.rendered)}"
    prod_template = "${base64encode(data.template_file.prod_sam_template.rendered)}"
  }
}

data "template_file" "qa_sam_template" {
  template = file("${path.module}/templates/sam-template.yaml")
  vars = {
    sns_CarrierChanged = "${var.qa_topic_carrierchanged}"
    sns_AllShiftsBusy  = "${var.qa_topic_allshiftsbusy}"
  }
}

data "template_file" "prod_sam_template" {
  template = file("${path.module}/templates/sam-template.yaml")
  vars = {
    sns_CarrierChanged = "${var.prod_topic_carrierchanged}"
    sns_AllShiftsBusy  = "${var.prod_topic_allshiftsbusy}"
  }
}

module "build" {
  source                      = "https://s3-us-west-2.amazonaws.com/yopdev.artifacts/terraform-modules/codebuild_tr_v0.12.tar.gz"
  name                        = "${var.name}-${terraform.workspace}-build"
  timeout                     = 60
  environment_codebuild_image = "aws/codebuild/standard:4.0-20.03.13"
  buildspec                   = data.template_file.buildspec.rendered
  role                        = aws_iam_role.codebuild_role.id
  environment_privileged_mode = true
 
}

resource "aws_iam_role" "codebuild_role" {
  name_prefix = "${var.name}_codebuild_"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "codebuild.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "admin" {
  role       = aws_iam_role.codebuild_role.id
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
}
