CREATE TABLE `orders` (
    `id` BINARY(16) NULL,
    `order_time` DATETIME NULL,
    `status` VARCHAR(255) NULL,
    `buyer_id` BINARY(16) NULL,
    PRIMARY KEY (`id`));
