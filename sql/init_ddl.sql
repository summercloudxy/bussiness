/*
 Navicat Premium Data Transfer

 Source Server         : localhost(111111)
 Source Server Type    : MySQL
 Source Server Version : 80035
 Source Host           : localhost:3306
 Source Schema         : mercari

 Target Server Type    : MySQL
 Target Server Version : 80035
 File Encoding         : 65001

 Date: 01/07/2024 14:49:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mercari_item_record
-- ----------------------------
DROP TABLE IF EXISTS `mercari_item_record`;
CREATE TABLE `mercari_item_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `search_condition_id` int NULL DEFAULT NULL,
  `mercari_item_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mercari_item_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int NULL DEFAULT NULL,
  `current_price` int NULL DEFAULT NULL,
  `item_condition_id` tinyint NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  `sold_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `seller_id` int NULL DEFAULT NULL,
  `record_create_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id` ASC) USING BTREE,
  INDEX `mercari_item_id`(`mercari_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2115282516 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mercari_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `mercari_search_condition`;
CREATE TABLE `mercari_search_condition`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `en_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文品名',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `item_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态，以逗号分隔，见ConditionEnum',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 420 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mercari_seller_item_record
-- ----------------------------
DROP TABLE IF EXISTS `mercari_seller_item_record`;
CREATE TABLE `mercari_seller_item_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `mercari_item_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mercari_item_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int NULL DEFAULT NULL,
  `current_price` int NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  `sold_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `seller_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mercari_item_id`(`mercari_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2115243554 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for mercari_seller_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `mercari_seller_search_condition`;
CREATE TABLE `mercari_seller_search_condition`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '包含关键字',
  `exclude_keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过滤关键字',
  `seller_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卖家id',
  `root_category` int NULL DEFAULT 6 COMMENT '过滤类别',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过滤品牌，以逗号分隔',
  `item_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态，以逗号分隔，见ConditionEnum',
  `duration` int NULL DEFAULT 5,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 352 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rakuten_item_record
-- ----------------------------
DROP TABLE IF EXISTS `rakuten_item_record`;
CREATE TABLE `rakuten_item_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `search_condition_id` int NULL DEFAULT NULL,
  `item_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int NULL DEFAULT NULL,
  `current_price` int NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_date` datetime NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `item_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74453 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rakuten_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `rakuten_search_condition`;
CREATE TABLE `rakuten_search_condition`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `search_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `max_page_num` int NULL DEFAULT 1,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 254 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for yahoo_item_record
-- ----------------------------
DROP TABLE IF EXISTS `yahoo_item_record`;
CREATE TABLE `yahoo_item_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `search_condition_id` int NULL DEFAULT NULL,
  `auction_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `interest` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `origin_price` int NULL DEFAULT NULL,
  `auction_price` int NULL DEFAULT NULL,
  `buy_now_price` int NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `is_paypal` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0,
  `is_new` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2136408547 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for yahoo_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `yahoo_search_condition`;
CREATE TABLE `yahoo_search_condition`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键字',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `search_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索类别，以逗号分隔，见CategoryEnum',
  `price_max` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `price_min` int(10) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '搜索价格区间',
  `duration` int NULL DEFAULT NULL COMMENT '间隔多长时间进行搜索，单位分钟',
  `start_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `end_time` datetime NULL DEFAULT NULL COMMENT '在哪个时间段进行搜索',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `search_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `page_size` int NULL DEFAULT 20,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 242 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
