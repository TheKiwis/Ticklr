ALTER TABLE `events` 
CHANGE COLUMN `user_id` `user_id` BINARY(16) NOT NULL ;
ALTER TABLE `baskets` 
CHANGE COLUMN `user_id` `user_id` BINARY(16) NOT NULL ;
