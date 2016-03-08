CREATE TABLE `basket_items` (
  `basket_item_id` INT NOT NULL AUTO_INCREMENT,
  `basket_id` INT NOT NULL,
  `tiket_set_id` INT NOT NULL,
  `price` INT NULL,
  `quantity` INT NULL,
  `created_time` DATETIME NULL,
  `updated_time` DATETIME NOT NULL,
  PRIMARY KEY (`basket_item_id`));

