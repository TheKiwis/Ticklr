CREATE TABLE `tickets` (
  `id` BINARY(16) NOT NULL,
  `first_name` VARCHAR(255) NULL,
  `last_name` VARCHAR(255) NULL,
  `usage_time` DATETIME NULL,
  `order_position_id` INT NULL,
  PRIMARY KEY (`id`));
