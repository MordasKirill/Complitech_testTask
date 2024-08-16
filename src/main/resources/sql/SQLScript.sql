CREATE DATABASE `test` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
use test;

CREATE TABLE `gender` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(45) DEFAULT NULL,
  `password` varchar(80) DEFAULT NULL,
  `full_name` varchar(45) DEFAULT NULL,
  `gender_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_User_Gender_idx` (`gender_id`),
  CONSTRAINT `fk_User_Gender` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;

/*
	Here password is 'test'
*/
insert into test.user(id, login, password, full_name, gender_id) values (1, 'test', '$2a$12$FuJ7h8m3In.9TFyEtKShyOyE9/eBKl/J2/LOu6MMnAjD.l9iWGR0e', 'Test', 1);

/*
	Here password is 'admin'
*/
insert into test.user(id, login, password, full_name, gender_id) values (2, 'admin', '$2a$12$MqkmvOT6b8DStz7NHQbMEeJJM0pwM/s7ZZus2lzKWR9jFI2bOermW', 'Admin', 2);

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_users_in_range`(IN start_id BIGINT, IN end_id BIGINT)
BEGIN
    DELETE FROM user WHERE id BETWEEN start_id AND end_id;
END$$
DELIMITER ;
