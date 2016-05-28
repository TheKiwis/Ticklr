ALTER TABLE `events` 
CHANGE COLUMN `user_id` `user_id` BINARY(16) NOT NULL ;
ALTER TABLE `baskets` 
CHANGE COLUMN `buyer_id` `buyer_id` BINARY(16) NOT NULL ;
