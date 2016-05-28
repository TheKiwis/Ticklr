ALTER TABLE `users` 
DROP COLUMN `password`,
DROP COLUMN `email`,
DROP INDEX `email_UNIQUE`;
