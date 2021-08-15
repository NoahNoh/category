# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.24)
# Database: Noah
# Generation Time: 2021-08-15 15:01:23 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table Category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Category`;

CREATE TABLE `Category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '카테고리 ID',
  `categoryName` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '카테고리 명',
  `categoryDesc` text COLLATE utf8_unicode_ci COMMENT '카테고리 설명',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `updatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table CategoryBundle
# ------------------------------------------------------------

DROP TABLE IF EXISTS `CategoryBundle`;

CREATE TABLE `CategoryBundle` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '카테고리 번들 ID',
  `mainCategoryId` int(11) NOT NULL COMMENT '메인 카테고리 ID',
  `subCategoryId` int(11) NOT NULL COMMENT '서브 카테고리 ID',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `updatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`id`),
  KEY `mainCategoryId` (`mainCategoryId`),
  KEY `subCategoryId` (`subCategoryId`),
  CONSTRAINT `CategoryBundle_ibfk_1` FOREIGN KEY (`mainCategoryId`) REFERENCES `Category` (`id`),
  CONSTRAINT `CategoryBundle_ibfk_2` FOREIGN KEY (`subCategoryId`) REFERENCES `Category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
