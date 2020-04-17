terraform {
  backend "s3" {
    bucket         = "ggsotillo-terraform-state"
    key            = "wabi"
    region         = "us-east-1"
    dynamodb_table = "terraform_lock"
  }
}
/*
data "terraform_remote_state" "foundation" {
  backend   = "s3"
  workspace = terraform.workspace

  config = {
    bucket = "wabilytics-terraform-state"
    key    = "foundation"
    region = var.region
  }
}

data "terraform_remote_state" "repositories" {
  backend = "s3"

  config = {
    bucket = "wabilytics-terraform-state"
    key    = "repositories"
    region = var.region
  }
}
*/