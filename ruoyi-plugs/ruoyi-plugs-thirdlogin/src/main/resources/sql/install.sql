/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : 127.0.0.1:3306
Source Database       : ry

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2021-07-23 15:28:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_third_oauth
-- ----------------------------
DROP TABLE IF EXISTS `sys_third_oauth`;
CREATE TABLE `sys_third_oauth` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `openid` varchar(50) DEFAULT NULL COMMENT '第三方平台用户ID',
  `login_type` varchar(50) DEFAULT NULL COMMENT '登录类型',
  `bind_time` datetime DEFAULT NULL COMMENT '绑定时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
