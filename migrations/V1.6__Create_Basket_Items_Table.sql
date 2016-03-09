CREATE TABLE `basket_items` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `basket_id` INT NOT NULL,
  `tiket_set_id` INT NULL, -- TODO: NOT NULL
  `unit_price` DECIMAL(10,0) NULL DEFAULT 0,
  `quantity` INT NULL,
  `created_time` DATETIME NULL,
  `updated_time` DATETIME NULL,
  PRIMARY KEY (`id`));

