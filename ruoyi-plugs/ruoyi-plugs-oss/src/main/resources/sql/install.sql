/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : 127.0.0.1:3306
Source Database       : ry

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2021-07-23 15:37:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) NOT NULL DEFAULT '' COMMENT '文件名',
  `file_suffix` varchar(10) NOT NULL DEFAULT '' COMMENT '文件后缀名',
  `url` varchar(200) NOT NULL COMMENT 'URL地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '上传人',
  `service` tinyint(2) NOT NULL DEFAULT '1' COMMENT '服务商',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='文件上传';
