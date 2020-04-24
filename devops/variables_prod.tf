variable "prod_db_url" {
  default = "jdbc:mysql://database-1.cbfzuvbm5tmc.us-east-1.rds.amazonaws.com/reporting"
}

variable "prod_db_username" {
  default = "reporting"
}

variable "prod_db_password" {
  default = "example"
}

variable "prod_topic_allshiftsbusy" {
  default = "arn:aws:sns:us-east-1:835328222444:eventos"
}

variable "prod_topic_carrierchanged" {
  default = "arn:aws:sns:us-east-1:835328222444:eventos"
}
