CREATE TABLE `basket_items` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `basket_id` INT NOT NULL,
  `ticket_set_id` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NULL DEFAULT 0,
  `quantity` INT NULL,
  `created_time` DATETIME NULL,
  `updated_time` DATETIME NULL,
  PRIMARY KEY (`id`));

