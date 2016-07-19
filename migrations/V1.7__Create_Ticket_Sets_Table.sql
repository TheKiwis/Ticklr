CREATE TABLE `ticket_sets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `price` DECIMAL(10,2) NULL DEFAULT 0,
  `title` VARCHAR(60) NOT NULL,
  `event_id` INT NOT NULL,
  PRIMARY KEY (`id`));
