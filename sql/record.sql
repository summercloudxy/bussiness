/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : mercari

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 15/09/2022 17:24:10
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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2115216666 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `item_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14056 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `search_condition_id`(`search_condition_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2136217566 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
