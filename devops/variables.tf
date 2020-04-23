variable "region" {
  default = "us-east-1"
}

variable "name" {
  default = "wabi"
}

variable "repository_name" {
  // default = data.terraform_remote_state.repositories.outputs.wabi
  default = "wabi"
}

variable "repository_branch" {
  // default = terraform.workspace
  default = "feature/onCarrierChanged"
}
