variable "region" {
  default = "us-east-1"
}

variable "name" {
  default = "wabi"
}

variable "repository_name" {
  default = "wabi"
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
