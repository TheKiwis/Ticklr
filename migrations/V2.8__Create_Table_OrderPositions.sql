CREATE TABLE `order_positions` (
  `id` INT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NULL,
  `quantity` INT NULL,
  `unit_price` DECIMAL(10,2) NULL,
  `order_id` BINARY(16) NULL,
  `ticket_set_id` INT NULL,
  PRIMARY KEY (`id`));
