CREATE TABLE `ticket_sets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `price` INT NULL,
  `title` VARCHAR(60) NOT NULL,
  `event_id` INT NOT NULL,
  PRIMARY KEY (`id`));
