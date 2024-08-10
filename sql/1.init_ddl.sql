/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80400
 Source Host           : localhost:3306
 Source Schema         : mercari

 Target Server Type    : MySQL
 Target Server Version : 80400
 File Encoding         : 65001

 Date: 10/08/2024 10:57:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mercari_item_record
-- ----------------------------
DROP TABLE IF EXISTS `mercari_item_record`;
CREATE TABLE `mercari_item_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `search_condition_id` int(0) NULL DEFAULT NULL,
  `mercari_item_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mercari_item_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int(0) NULL DEFAULT NULL,
  `current_price` int(0) NULL DEFAULT NULL,
  `item_condition_id` tinyint(0) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `sold_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `seller_id` int(0) NULL DEFAULT NULL,
  `record_create_date` datetime(0) NULL DEFAULT NULL,
  `exclude` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE,
  INDEX `mercari_item_id`(`mercari_item_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2115287481 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mercari_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `mercari_search_condition`;
CREATE TABLE `mercari_search_condition`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `en_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文品名',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int(0) NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `item_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态，以逗号分隔，见ConditionEnum',
  `exclude_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '排除关键字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 440 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mercari_seller_item_record
-- ----------------------------
DROP TABLE IF EXISTS `mercari_seller_item_record`;
CREATE TABLE `mercari_seller_item_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `mercari_item_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mercari_item_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int(0) NULL DEFAULT NULL,
  `current_price` int(0) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `sold_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `seller_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mercari_item_id`(`mercari_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mercari_seller_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `mercari_seller_search_condition`;
CREATE TABLE `mercari_seller_search_condition`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '包含关键字',
  `exclude_keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过滤关键字',
  `seller_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卖家id',
  `root_category` int(0) NULL DEFAULT 6 COMMENT '过滤类别',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过滤品牌，以逗号分隔',
  `item_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态，以逗号分隔，见ConditionEnum',
  `duration` int(0) NULL DEFAULT 5,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 350 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rakuten_item_record
-- ----------------------------
DROP TABLE IF EXISTS `rakuten_item_record`;
CREATE TABLE `rakuten_item_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `search_condition_id` int(0) NULL DEFAULT NULL,
  `item_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int(0) NULL DEFAULT NULL,
  `current_price` int(0) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `update_date` datetime(0) NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `item_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `exclude` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78203 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rakuten_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `rakuten_search_condition`;
CREATE TABLE `rakuten_search_condition`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int(0) NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `search_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `max_page_num` int(0) NULL DEFAULT 1,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `exclude_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '排除关键字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 256 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for yahoo_item_record
-- ----------------------------
DROP TABLE IF EXISTS `yahoo_item_record`;
CREATE TABLE `yahoo_item_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `search_condition_id` int(0) NULL DEFAULT NULL,
  `auction_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int(0) NULL DEFAULT NULL,
  `auction_price` int(0) NULL DEFAULT NULL,
  `buy_now_price` int(0) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `is_paypal` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0,
  `is_new` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `exclude` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2136425595 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for yahoo_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `yahoo_search_condition`;
CREATE TABLE `yahoo_search_condition`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int(0) NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `search_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `page_size` int(0) NULL DEFAULT 20,
  `exclude_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '排除关键字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 244 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
