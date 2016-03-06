CREATE TABLE `events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` TEXT(5000) NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `description` LONGTEXT NULL,
  `visibility` VARCHAR(45) NULL COMMENT 'possible values: public, private',
  `canceled` BIT(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`));
