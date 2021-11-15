/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : 127.0.0.1:3306
Source Database       : ry

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2021-07-23 15:31:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for third_ai_his
-- ----------------------------
DROP TABLE IF EXISTS `third_ai_his`;
CREATE TABLE `third_ai_his` (
  `id` varchar(50) NOT NULL COMMENT 'ID',
  `yhid` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `yhmc` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `ai_type` varchar(50) DEFAULT NULL COMMENT '类型',
  `type_name` varchar(50) DEFAULT NULL COMMENT '类型名称',
  `result` varchar(20) DEFAULT NULL COMMENT '结果1成功0失败',
  `error_msg` text COMMENT '错误信息',
  `json_result` text COMMENT '请求结果',
  `create_time` datetime DEFAULT NULL COMMENT '请求时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for third_sms_his
-- ----------------------------
DROP TABLE IF EXISTS `third_sms_his`;
CREATE TABLE `third_sms_his` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `yhid` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `yhmc` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `carrieroperator` varchar(50) DEFAULT NULL COMMENT '运营商',
  `phone` varchar(45) DEFAULT NULL COMMENT '手机号',
  `content` varchar(500) DEFAULT NULL COMMENT '内容',
  `returncode` varchar(200) DEFAULT NULL COMMENT '返回码',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
