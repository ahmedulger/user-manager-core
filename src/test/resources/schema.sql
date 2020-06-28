-- Created for testing on H2 database
CREATE TABLE user (
  user_id int NOT NULL,
  credential varchar(512) NOT NULL,
  email varchar(255) NOT NULL,
  first_name varchar(32) NOT NULL,
  last_name varchar(32) NOT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY IX_1 (email)
)