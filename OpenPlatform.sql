--
--create database open_platform;
--
--use open_platform;
--
---- ----------------------------
--
---- Table structure for `bcuser`
--
---- ----------------------------
--
--DROP TABLE IF EXISTS `bcuser`;
--
--CREATE TABLE `bcuser` (
--
--`user_id` bigint(20) NOT NULL AUTO_INCREMENT,
--
--`created` datetime default current_timestamp COMMENT '创建时间',
--
--`updated` datetime default current_timestamp COMMENT '修改时间',
--
--`status` tinyint(255) DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
--
--`mobile` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '手机',
--
--`password` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '密码',
--
--`salt` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '盐',
--
--PRIMARY KEY (`user_id`)
--
--) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
--
--
--
---- ----------------------------
--
---- Table structure for `application`
--
---- ----------------------------
--
--DROP TABLE IF EXISTS `application`;
--
--CREATE TABLE `application` (
--  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
--  `app_id` varchar(255) NOT NULL COMMENT '应用唯一识别码' ,
--  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
--  `status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
--  `user_id` bigint(20) NOT NULL COMMENT '用户id',
--  `app_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',
--  `app_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用类型',
--  `app_status` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用状态',
--  `app_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用key',
--  `app_secret` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用秘钥',
--  `env_type` tinyint(4) DEFAULT '1' COMMENT '1-dev 2-test 3-pre 4-pro',
--  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用描述',
--  `app_store` tinyint(2) DEFAULT '1' COMMENT '0-无 1-有  appstore 有无应用',
--  `android` tinyint(2) DEFAULT '1' COMMENT '0 -无 1-有',
--  `web_site` varchar(255) COLLATE utf8_bin DEFAULT NULL,
--  PRIMARY KEY (`id`),
--  UNIQUE KEY `app_key` (`app_key`),
--  UNIQUE KEY `app_secret` (`app_secret`)
--) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COLLATE=utf8_bin
--
--
---- test 脚本 start----------------------------
--
---- ----------------------------
--
---- 初始化 测试用户数据
--
---- ----------------------------
--
--INSERT INTO `bcuser` VALUES (1, NOW(), NOW(),1,'13000000000','123456','ABCD');
--
--INSERT INTO `bcuser` VALUES (2, NOW(), NOW(),1,'131111111111','123456','ABCD');
--
--INSERT INTO `bcuser` VALUES (3, NOW(), NOW(),1,'13222222222','123456','ABCD');
--
--
--
---- ----------------------------
--
---- 初始化 测试用户应用
--
---- ----------------------------
--
--INSERT INTO `application` VALUES (1, NOW(), NOW(),1,1,'众筹','A','A','1','ABCD');
--
--INSERT INTO `application` VALUES (2, NOW(), NOW(),1,2,'征信','A','A','12','EFGH');
--
--INSERT INTO `application` VALUES (3, NOW(), NOW(),1,3,'监管','A','A','13','ASDF');
--
---- test 脚本 end------------------------------
--
--
--
--
--
--
--
--
--
--
--
--SET NAMES utf8;
--
--SET FOREIGN_KEY_CHECKS = 0;
--
---- ----------------------------
--
---- Table structure for `app_address`
--
---- ----------------------------
--
--DROP TABLE IF EXISTS `app_address`;
--
--CREATE TABLE `app_address` (
--
--`id` bigint(20) NOT NULL AUTO_INCREMENT,
--
--`created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--
--`updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
--
--`status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
--
--`app_id` bigint(20) NOT NULL COMMENT '应用id',
--
--`type` int(2) DEFAULT '1' COMMENT '链类型0-共有链 1-私有链',
--
--`address_from` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转出地址',
--
--`address_to` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转入地址',
--
--`app_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户appkey',
--
--`password` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '地址密码',
--
--PRIMARY KEY (`id`)
--
--) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
--
--SET FOREIGN_KEY_CHECKS = 1;
--
--
--
--
--
--
--
--/*
--
--Navicat Premium Data Transfer
--
--Source Server : open_platform
--
--Source Server Type : MySQL
--
--Source Server Version : 50720
--
--Source Host : 47.100.175.16
--
--Source Database : open_platform
--
--Target Server Type : MySQL
--
--Target Server Version : 50720
--
--File Encoding : utf-8
--
--Date: 03/27/2018 16:10:47 PM
--
--*/
--
--SET NAMES utf8;
--
--SET FOREIGN_KEY_CHECKS = 0;
--
---- ----------------------------
--
---- Table structure for `data_chain`
--
---- ----------------------------
--
--DROP TABLE IF EXISTS `data_chain`;
--
--CREATE TABLE `data_chain` (
--
--`id` bigint(20) NOT NULL AUTO_INCREMENT,
--
--`created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--
--`updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
--
--`status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
--
--`nonce` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'nonce',
--
--`address_from` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转出地址',
--
--`address_to` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转入地址',
--
--`data` text COLLATE utf8_bin COMMENT '上链数据',
--
--`receipt` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '上链返回数据',
--
--`data_status` tinyint(3) DEFAULT '1' COMMENT '状态 1:penging 2:sucess 3:falied',
--
--`message` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '接口返回信息',
--
--PRIMARY KEY (`id`)
--
--) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
--
--SET FOREIGN_KEY_CHECKS = 1;

create database open_platform;

use open_platform;

-- ----------------------------

-- Table structure for `bcuser`

-- ----------------------------

DROP TABLE IF EXISTS `bcuser`;

CREATE TABLE `bcuser` (

`user_id` bigint(20) NOT NULL AUTO_INCREMENT,

`created` datetime default current_timestamp COMMENT '创建时间',

`updated` datetime default current_timestamp COMMENT '修改时间',

`status` tinyint(255) DEFAULT 1 COMMENT '状态 0:禁用，1:正常',

`mobile` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '手机',

`password` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '密码',

`salt` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '盐',

PRIMARY KEY (`user_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



-- ----------------------------

-- Table structure for `application`

-- ----------------------------

DROP TABLE IF EXISTS `application`;

CREATE TABLE `application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_id` varchar(255) NOT NULL COMMENT '应用唯一识别码' ,
  `created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `app_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',
  `app_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用类型',
  `app_status` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用状态',
  `app_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用key',
  `app_secret` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用秘钥',
  `env_type` tinyint(4) DEFAULT '1' COMMENT '1-dev 2-test 3-pre 4-pro',
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用描述',
  `app_store` tinyint(2) DEFAULT '1' COMMENT '0-无 1-有  appstore 有无应用',
  `android` tinyint(2) DEFAULT '1' COMMENT '0 -无 1-有',
  `web_site` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_key` (`app_key`),
  UNIQUE KEY `app_secret` (`app_secret`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- test 脚本 start----------------------------

-- ----------------------------

-- 初始化 测试用户数据

-- ----------------------------

INSERT INTO `bcuser` VALUES (1, NOW(), NOW(),1,'13000000000','123456','ABCD');

INSERT INTO `bcuser` VALUES (2, NOW(), NOW(),1,'131111111111','123456','ABCD');

INSERT INTO `bcuser` VALUES (3, NOW(), NOW(),1,'13222222222','123456','ABCD');



-- ----------------------------

-- 初始化 测试用户应用

-- ----------------------------

INSERT INTO `application` VALUES (1,'1',NOW(),NOW(),1,1,'众筹','A','A','A','A',1,'A',1,1,'A');

INSERT INTO `application` VALUES (2,'1',NOW(),NOW(),1,1,'征信','B','B','B','B',1,'A',1,1,'B');

INSERT INTO `application` VALUES (3,'1',NOW(),NOW(),1,1,'监管','C','C','C','C',1,'C',1,1,'C');

-- test 脚本 end------------------------------











SET NAMES utf8;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------

-- Table structure for `app_address`

-- ----------------------------

DROP TABLE IF EXISTS `app_address`;

CREATE TABLE `app_address` (

`id` bigint(20) NOT NULL AUTO_INCREMENT,

`created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

`updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',

`status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',

`app_id` bigint(20) NOT NULL COMMENT '应用id',

`type` int(2) DEFAULT '1' COMMENT '链类型0-共有链 1-私有链',

`address_from` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转出地址',

`address_to` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转入地址',

`app_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户appkey',

`password` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '地址密码',

PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;







/*

Navicat Premium Data Transfer

Source Server : open_platform

Source Server Type : MySQL

Source Server Version : 50720

Source Host : 47.100.175.16

Source Database : open_platform

Target Server Type : MySQL

Target Server Version : 50720

File Encoding : utf-8

Date: 03/27/2018 16:10:47 PM

*/

SET NAMES utf8;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------

-- Table structure for `data_chain`

-- ----------------------------

DROP TABLE IF EXISTS `data_chain`;

CREATE TABLE `data_chain` (

`id` bigint(20) NOT NULL AUTO_INCREMENT,

`created` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

`updated` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',

`status` tinyint(255) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',

`nonce` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'nonce',

`address_from` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转出地址',

`address_to` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '转入地址',

`data` text COLLATE utf8_bin COMMENT '上链数据',

`receipt` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '上链返回数据',

`data_status` tinyint(3) DEFAULT '1' COMMENT '状态 1:penging 2:sucess 3:falied',

`message` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '接口返回信息',

PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;