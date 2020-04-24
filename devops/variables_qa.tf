variable "qa_db_url" {
  default = "jdbc:mysql://database-1.cbfzuvbm5tmc.us-east-1.rds.amazonaws.com/qa_reporting"
}

variable "qa_db_username" {
  default = "reporting"
}

variable "qa_db_password" {
  default = "example"
}

variable "qa_topic_allshiftsbusy" {
  default = "arn:aws:sns:us-east-1:835328222444:qa_eventos"
}

variable "qa_topic_carrierchanged" {
  default = "arn:aws:sns:us-east-1:835328222444:qa_eventos"
}