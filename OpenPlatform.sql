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

  `app_id` bigint(20) NOT NULL AUTO_INCREMENT,

  `created` datetime default current_timestamp COMMENT '创建时间',

  `updated` datetime default current_timestamp COMMENT '修改时间',

  `status` tinyint(255) DEFAULT 1 COMMENT '状态 0:禁用，1:正常',

  `user_id` bigint(20) NOT NULL COMMENT '用户id',

  `app_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',

  `app_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用类型',

  `app_status` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '应用状态',

  `app_key` varchar(255) COLLATE utf8_bin DEFAULT NULL unique COMMENT '应用key  唯一性',

  `app_secret` varchar(255) COLLATE utf8_bin DEFAULT NULL unique COMMENT '应用秘钥  唯一性',

  PRIMARY KEY (`app_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



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

INSERT INTO `application` VALUES (1, NOW(), NOW(),1,1,'众筹','A','A','1','ABCD');

INSERT INTO `application` VALUES (2, NOW(), NOW(),1,2,'征信','A','A','12','EFGH');

INSERT INTO `application` VALUES (3, NOW(), NOW(),1,3,'监管','A','A','13','ASDF');

-- test 脚本 end------------------------------