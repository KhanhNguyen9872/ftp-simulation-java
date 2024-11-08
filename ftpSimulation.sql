create database ftpSimulation;
use ftpSimulation;

CREATE TABLE `account` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  primary key (`username`)
);

CREATE TABLE `settings` (
  `id` int(1),
  `path` varchar(255) NOT NULL,
  `port` int(5) NOT NULL,
  primary key (`id`)
);

DELETE FROM `account`;
DELETE FROM `settings`;

INSERT INTO `account` (`username`, `password`) VALUES
('root', 'root');
INSERT INTO `settings` (`id`, `path`, `port`) VALUES
(1, 'c:\\Users\\KhanhNguyen9872\\Documents\\GitHub\\ftp-simulation-java', 8021);

SELECT * FROM account;
SELECT * FROM settings;
