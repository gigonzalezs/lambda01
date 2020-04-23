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

variable "db_url" {
  default = "jdbc:mysql://database-1.cbfzuvbm5tmc.us-east-1.rds.amazonaws.com/reporting"
}

variable "db_username" {
  default = "reporting"
}

variable "db_password" {
  default = "example"
}
