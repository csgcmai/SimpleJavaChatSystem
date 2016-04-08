-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2013 年 07 月 31 日 09:05
-- 服务器版本: 5.6.12-log
-- PHP 版本: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `chatsystem`
--
CREATE DATABASE IF NOT EXISTS `chatsystem` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `chatsystem`;

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `password` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `usertype` int(4) NOT NULL DEFAULT '0',
  `real_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `company_name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `phone` bigint(20) DEFAULT NULL,
  `email` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `lastupdatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `onlineip` int(4) NOT NULL DEFAULT '0',
  `onlineport` int(4) NOT NULL DEFAULT '0',
  `comment` text COLLATE utf8_bin,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=25 ;

--
-- 转存表中的数据 `user`
--

INSERT INTO `user` (`id`, `name`, `password`, `usertype`, `real_name`, `company_name`, `phone`, `email`, `lastupdatetime`, `onlineip`, `onlineport`, `comment`) VALUES
(2, 'mgc2', '11', 2, '李钟铭', '1324', 15889937255, '132455', '0000-00-00 00:00:00', 0, 0, 'haha'),
(5, '12345', '123', 2, '12345', '13245', 12345, '13245@163.com', '0000-00-00 00:00:00', 0, 0, NULL),
(6, 'mgc', '123', 0, 'maiguangcan', 'scut', 1234, '12345', '0000-00-00 00:00:00', 0, 0, NULL),
(7, '3241234', '1', 0, '1', '1', 1, '1', '0000-00-00 00:00:00', 0, 0, NULL),
(8, '12', '123', 0, '12', '12', 12, '12', '0000-00-00 00:00:00', 0, 0, NULL),
(9, '34', '34', 0, '34', '34', 34, '34', '0000-00-00 00:00:00', 0, 0, NULL),
(11, '123', '123', 0, '123', '123', 123, '123', '0000-00-00 00:00:00', 0, 0, NULL),
(12, 'sdaf', 'a', 0, 'asdf', 'bbbbbbbb', 0, 'a', '0000-00-00 00:00:00', 0, 0, NULL),
(13, 'sdf', 'sdf', 0, 'sdf', 'sdf', 0, 'sdf', '0000-00-00 00:00:00', 0, 0, NULL),
(16, 'qq', 'qq', 0, 'qqq', 'qq', 0, 'qq', '0000-00-00 00:00:00', 0, 0, NULL),
(17, 'df', 'df', 0, 'sdf', 'df', 0, 'df', '0000-00-00 00:00:00', 0, 0, NULL),
(19, 'sdfsdf', 'sdf', 0, 'sdf', 'sdf', 0, 'sdf', '0000-00-00 00:00:00', 0, 0, NULL);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
