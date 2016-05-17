CREATE TABLE `events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` TEXT(5000) NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `description` LONGTEXT NULL,
  `canceled` BIT(1) NULL DEFAULT 0,
  `created_time` DATETIME NULL,
  `updated_time` DATETIME NULL,
  PRIMARY KEY (`id`));
