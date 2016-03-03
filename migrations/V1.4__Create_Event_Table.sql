CREATE TABLE `events` (
  `id` INT NULL AUTO_INCREMENT,
  `title` TEXT(5000) NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `description` LONGTEXT NULL,
  `status` VARCHAR(45) NULL COMMENT 'possible values: draft, published, deleted, canceled',
  `visibility` VARCHAR(45) NULL COMMENT 'possible values: public, private',
  PRIMARY KEY (`id`));
