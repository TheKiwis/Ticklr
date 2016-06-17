CREATE TABLE `paypal_payments` (
  `id` INT NOT NULL,
  `payment_id` VARCHAR(255) NOT NULL,
  `created_time` DATETIME NULL,
  PRIMARY KEY (`id`));
