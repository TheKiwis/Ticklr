CREATE TABLE `baskets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `created_time` DATETIME NOT NULL,
  `updated_time` DATETIME NULL,
  PRIMARY KEY (`id`));