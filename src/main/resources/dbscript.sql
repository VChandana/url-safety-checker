CREATE DATABASE url_safety
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE url_safety;
CREATE USER 'urlsafe'@'localhost' IDENTIFIED BY 'pass123';

GRANT ALL PRIVILEGES ON url_safety.* TO 'urlsafe'@'localhost';

FLUSH PRIVILEGES;

SELECT User, Host
FROM mysql.user
WHERE User = 'urlsafe';