CREATE TABLE test
(
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  email varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS blacklist (
  id integer,
  first_name citext,
  last_name citext,
  PRIMARY KEY (id)
);