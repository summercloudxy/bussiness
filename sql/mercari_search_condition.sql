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

 Date: 10/02/2023 17:20:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 354 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mercari_search_condition
-- ----------------------------
INSERT INTO `mercari_search_condition` VALUES (2, 'シャネル レティサージュ ラメ', NULL, '鸟笼', '', NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (3, 'シャネル メイクアップカラー', NULL, '珠珠', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (4, 'レ ペルル ドゥ シャネル', NULL, '珠珠', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (5, 'シャネル ルミエール ダルティフィス', NULL, '珠片和星球', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (6, 'プードゥルティセ', NULL, 'tissee', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (7, 'ジーンズ ドゥ シャネル', NULL, '牛仔', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (8, 'シャネル イレール グロス ', NULL, '晃动logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (10, 'アンタンス ラディアンス', NULL, '晃动logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (11, 'コロマンデル ドゥ シャネル', 'Coromandel de Chanel', '东方屏风', 'DIZHUANGALL,HUAZHUANGPINALL', NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (12, 'シャネル プルミエール フルール', NULL, '东方屏风', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (13, 'シャネル ペルル エ ファンテジー', NULL, '双色珠链', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (14, 'シャネル ピンク ラメ', NULL, '毛呢', 'YANXIANBI,YANYING', NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (16, 'シャネル  ソーシャネル', NULL, 'so chanel', 'HUAZHUANGPIN', NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (17, 'シャネル レーヴ ドリヤン', NULL, '星星四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (18, 'マルチ エフェクト パウダー アイシャドウ', NULL, '星球四色眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (19, 'クワトル マーブル ドゥ シャネル', NULL, '星球四色眼影', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (20, 'アンプラント ドゥ シャネル', NULL, '沙漠logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (21, 'トゥロワ リンニュ ドゥ シャネル', NULL, '黑眉粉', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (22, 'グワッシュ ドゥ シャネル', NULL, '调色盘', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (23, 'シャネル リュバン ペルレ', NULL, '九宫格粉饼', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (25, 'プードゥル シニエ', NULL, '米色印记', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (26, 'ルート デ ザンド ドゥ シャネル', 'route des indes de chanel', '孟买', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (27, 'ソーホー ドゥ シャネル', NULL, 'soho', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (28, 'シャネル ラッキー ストライプ', NULL, 'lucky', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (29, 'ルミエール スクルテ ドゥ シャネル', NULL, '雕花粉饼', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (30, 'シャネル ジャルダン ドゥ カメリア', NULL, '脑花粉饼', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (31, 'シャネル ロンドンマッドネス', 'chanel london madness', '英国伦敦', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (33, 'プードゥルインプレッションドゥシャネル', 'les 4 impressions de chanel', 'Poudre Impressions ', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (34, 'オンブル ティセ ベージュ', NULL, '粉色渐变高光', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (35, 'シャネル オンブルティセ', NULL, '粉色渐变高光', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (36, 'ルミエールダルティフィス', NULL, '钱币', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (37, 'プードゥルサテン ドゥ シャネル', NULL, '缎带', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (39, 'シャネル カメリア ドゥ プルム プラティン', NULL, '羽毛高光', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (40, 'シャネル デュオドゥカメリア', NULL, '双色高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (41, 'シャネル ヌワール ラメ', NULL, '星球唇膏', 'HUAZHUANGPINALL', NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (42, 'シャネル レーヴ ドゥ カメリア', NULL, '山茶花高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (43, 'シャネル ルミエール デテ', NULL, '山茶花高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (44, 'シャネル ドンテル プレシューズ', NULL, '蕾丝', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (45, 'ムーシュドゥボーテ', NULL, 'mouche', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (46, 'シャネル フルール セレスト', 'fleurs celestes de chanel', '花朵三色', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (47, 'ブラッシュ オリゾン ドゥ シャネル', NULL, '粉色白色条纹', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (48, 'ルガール シニエ ドゥ シャネル', NULL, '拜占庭四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (49, 'ディオール プリンセスリング', NULL, '戒指', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (50, 'フルール ドゥ ヴァン パレット', NULL, '蒲公英', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (51, 'ディオール フラワーブロッサム', NULL, '花园', 'YANXIANBI,DIZHUANGALL,YANYING,LIANJIA,LIANSE', NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (52, 'ディオール クリスタルボレアル', NULL, '凡尔赛', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (53, 'リップグロス ペンダント スワロスキー', NULL, '施华洛世奇', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (54, 'Diorプリティチャーム', NULL, '心形唇彩', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (55, 'スノー イリディセント パウダー', 'DIORSNOW LUMIÈRES DE NEIGE', '雪花粉饼', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (56, 'ディオール ディオリッシム', NULL, '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (57, 'ディオール ガーデンクラッチ', NULL, '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (58, 'Dior ミノディエール', NULL, '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (59, 'ディオール ホリデイ コレクション', NULL, 'holiday', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (60, 'プードリエ ダンテル', NULL, '蕾丝', NULL, NULL, NULL, 3, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (61, 'ディオールプレイ', NULL, '骰子', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (62, 'ミラノコレクション 21st', NULL, '21st天使粉', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (63, 'ミラノコレクション  2001', NULL, '2001', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (64, 'ミラノコレクション  2002', NULL, '2002', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (65, 'ミラノコレクション  2003', NULL, '2003', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (66, 'ミラノコレクション  2004', NULL, '2004', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (67, 'ミラノコレクション  2005', NULL, '2005', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (68, 'ミラノコレクション  2006', NULL, '2006', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (69, 'ミラノコレクション  2007', NULL, '2007', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (70, 'ミラノコレクション  2008', NULL, '2008', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (71, 'ミラノコレクション  2009', NULL, '2009', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (72, 'ミラノコレクション  2010', NULL, '2010', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (73, 'ランコム スパークリングシェルブ', NULL, '天使', NULL, NULL, NULL, 10, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (75, 'ゲラン ラディアント シャドウ', NULL, '眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (76, 'マルセル ワンダース', NULL, '蜜粉', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'decorte', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (77, 'アルビオン レガァーメ', NULL, '宝格丽蜜粉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'albion', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (78, 'YSL ジュエルリップスティック', NULL, '宝石口红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (79, 'ヴィンテージ イブサンローラン ジュエル リップステック', NULL, '宝石口红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (80, 'YSL ジュエルパウダー', NULL, '宝石粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (81, 'サンローランジュエルコンパクト ブローチ', NULL, '宝石粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (82, 'ゲラン ラディアント バタフライ', NULL, '蝴蝶夫人', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (83, 'クレ・ド・ポー ボーテ パレットヴィンテージ', NULL, '高光', NULL, NULL, NULL, 120, NULL, NULL, 1, 'cpb', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (84, '資生堂 七色粉白粉', NULL, '七色粉白粉', NULL, NULL, NULL, 30, NULL, NULL, 1, '资生堂', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (85, 'パレット パリジャンナイト ブラッシュ', NULL, '爱心腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (86, 'ラデュレ 6TH', NULL, '鸟笼', NULL, NULL, NULL, 2, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (87, 'マキアージュ スノービューティーII', NULL, '雪花粉饼2015', NULL, NULL, NULL, 10, NULL, NULL, 0, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (88, '資生堂 レ プードリエ オン ドゥ', NULL, '人像壳子腮红眼影', NULL, NULL, NULL, 60, NULL, NULL, 1, '资生堂', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (89, 'クレドポー ボーテ パレット ド・ミル カラ', NULL, '高光同款唇膏', NULL, NULL, NULL, 240, NULL, NULL, 0, 'cpb', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (90, 'パウダーセレブレーション', NULL, '粉末庆典', NULL, NULL, NULL, 60, NULL, NULL, 1, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (91, 'ラ・プードル ルイスロント', NULL, '花朵粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, '资生堂', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (92, 'ジルスチュアート 練り香水', NULL, '固体香膏', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (93, 'ジルスチュアート ダイアモンドデュウ リップカラー', NULL, '戒指', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (94, 'ヴィンテージジュエル コンパクトミラー', NULL, '戒指同款镜子', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (95, 'ローズリベルテ', NULL, '黄色玫瑰', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (96, 'バタフライフィーバー', NULL, '蝴蝶', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (97, 'メゾン ランコム', NULL, 'maison', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (98, ' スノービューティー  2014', NULL, '雪花粉饼2014', NULL, NULL, NULL, 30, NULL, NULL, 1, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (99, 'スノービューティー  初代', NULL, '雪花粉饼2014', NULL, NULL, NULL, 30, NULL, NULL, 1, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (100, ' スノービューティー  白雪姫', NULL, '雪花粉饼白雪', NULL, 0000006000, NULL, 30, NULL, NULL, 1, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (101, 'クレ・ド・ポー ボーテ プードルコンパクト', NULL, '粉饼', NULL, NULL, NULL, 120, NULL, NULL, 0, 'cpb', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (102, 'クワトル ブトン ドゥ シャネル アイシャドウ', NULL, '彩色logo眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (103, 'QUATUOR BOUTONS DE CHANEL', NULL, '彩色logo眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (104, 'ディオールディオール スノウ', 'Dior snow fresh cheeks', '雪精灵', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (105, 'カネボウ ベルミュージアム コンパクト', NULL, '93年悬赏品', NULL, NULL, NULL, 60, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (106, 'イヴ・サンローラン ラブコレクション', NULL, '爱心', NULL, NULL, NULL, 120, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (107, '資生堂 プードル コーディアル', NULL, '陶瓷蜜粉', NULL, NULL, NULL, 60, NULL, NULL, 0, '资生堂', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (108, 'プードルクルーズエキゾチック', NULL, '异国情调粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, 'givenchy', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (109, 'ラデュレ ポット ローズ', NULL, '花瓣腮红', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (110, 'ラデュレ リミテッドエディション', NULL, '限量腮红', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (111, 'ラデュレ 花びら', NULL, '花瓣腮红', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (113, 'Dior グランバル 001', NULL, '金灿', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (114, 'Dior NIGHT DIAMOND', NULL, 'night diamond', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (115, 'バーバリーファーストラブ', NULL, 'first love', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'Burberry', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (116, 'メテオリット ヴォワイヤージュ', NULL, '蝴蝶/蜜蜂', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (117, 'ディオール スキンヌード エアーパウダー コンパクト', NULL, '老版nude', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (118, 'ヌード トランザット パウダー', NULL, '编织nude', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (119, 'ディオリフィック パウダー', NULL, 'diorific', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (120, 'ルナソル パーティアイズ 2014', NULL, '扑克牌', '', NULL, NULL, 60, NULL, NULL, 0, 'lunasol', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (121, 'クリスチャン ディオール トリアノン パレット', NULL, '蝴蝶结手包', '', NULL, NULL, 30, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (122, 'Dior ディオール シェリー ボウ パレット ', NULL, '蝴蝶结手包', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (123, 'ラデュレ アイシャドウ', NULL, '帽子眼影', NULL, NULL, NULL, 120, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (124, 'Dior アイシャドウ ジャズクラブ', NULL, 'jazzclub', NULL, NULL, NULL, 60, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (125, 'エスティ ローダー ワールドトラベルリップケース', NULL, '世界旅行', 'HUAZHUANGPIN', NULL, NULL, 240, NULL, NULL, 1, 'estee', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (126, 'estee world', NULL, '世界旅行', 'HUAZHUANGPIN', NULL, NULL, 240, NULL, NULL, 1, 'estee', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (127, 'バーバリー ゴールドグロウ フレグランス', NULL, '蝴蝶结', NULL, NULL, NULL, 120, NULL, NULL, 1, 'Burberry', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (128, 'ディオール アンセルムライル パレット', NULL, '蓝色丛林眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (129, 'ディオール ナイト ダイヤモンド', NULL, 'night diamond', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (130, 'イヴサンローラン ユアシークレット ラグジュアリー', NULL, '手链唇膏香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (131, 'ディオール ブラッシュハーモニー', NULL, 'harmony', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (132, 'ポール ジョー シマリングプレストパウダー', NULL, '蝴蝶', NULL, NULL, NULL, 30, NULL, NULL, 1, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (133, 'クリスチャンディオール オリガミ', NULL, '折纸', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (134, 'マイ レディー ブラッシュ', NULL, '编织腮红', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (135, 'Dior ビューティコンフィデンシャル', NULL, '信封', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (136, 'ランコム ルーシアイズ', NULL, '贝壳', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (137, 'ランコム ルーシーアイズ', NULL, '贝壳', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (138, 'ラデュレ プレストフェイスパウダー', NULL, '蛋糕', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (139, 'ランコム パレット ブラゾン', NULL, 'logo款腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (140, 'dior 441', NULL, '441花朵', 'YANXIANBI,YANYING', NULL, NULL, 3, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (141, 'dior 841', NULL, '841花朵', 'YANXIANBI,YANYING', NULL, NULL, 3, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (143, 'dior 059', NULL, '059蕾丝', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (144, 'dior 743', NULL, '743蕾丝', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (145, 'dior 724', NULL, '724格纹眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (146, 'dior 451', NULL, '451繁花', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (147, 'dior 031', NULL, '031繁花', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (148, 'dior 776', NULL, '776钱币眼影', NULL, 0000004000, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (149, 'dior 717', NULL, '717', NULL, 0000005000, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (150, ' エレガンス ファンタムアイズ', NULL, '眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'elegance', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (151, 'オンブル ラメ ドゥ シャネル', NULL, '麦穗', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (152, 'dior 671', NULL, '671钱币腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (153, 'dior 844', NULL, '844花朵腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (154, 'dior 442', NULL, '442花朵腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (155, 'dior 536', NULL, '536南瓜车腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (156, 'dior 946', NULL, '946蝴蝶结腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (157, 'スノービューティー Ⅱ', NULL, '心机粉饼2015', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 0, 'maquillage', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (158, 'ナイトジュエルソリッドパフューム', NULL, 'night固体香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (159, 'ジルスチュアート クリスタルブルーム ソリッドパフューム', NULL, '花朵固体香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (160, 'RMK トランスルーセント チークス', NULL, '小花花', NULL, NULL, NULL, 120, NULL, NULL, 1, 'rmk', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (161, 'ESTEE LAUDER ワールド', NULL, 'world travel', NULL, NULL, NULL, 240, NULL, NULL, 1, 'estee', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (162, 'ランコム　ロータススプレンダー', NULL, '莲花', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (163, 'ジルスチュアート クリスマス', NULL, '圣诞', 'HUAZHUANGPIN', 0000005000, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (164, 'ラデュレ ポット', NULL, '盅', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (165, 'dior ゴルメット', NULL, '手链唇膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (166, 'シャネル ブロンジングパウダー', NULL, 'logo修容粉饼', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (167, 'フェイスパレット モン パリフローラル', NULL, '蝴蝶结粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (168, 'イヴサンローラン モンパリ', NULL, '蝴蝶结粉饼', 'XIANGFEN', NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (169, 'スウィートネスコレクション', NULL, '2009圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', '');
INSERT INTO `mercari_search_condition` VALUES (171, 'ジルスチュアート マイディアストロベリー', NULL, '2013圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (172, 'ジルスチュアート スティーリングハート', NULL, '2012圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (173, 'ジルスチュアート フェアリーガーデン', NULL, '2011圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (174, 'ジルスチュアート シークレットティーズ', NULL, '2010圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (175, 'ジルスチュアート ホワイトラブストーリー', NULL, '2018圣诞', NULL, 0000004000, NULL, 120, NULL, NULL, 0, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (176, 'ジルスチュアート パジャマパーティ', NULL, '2017圣诞', NULL, 0000004000, NULL, 120, NULL, NULL, 0, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (177, 'ジルスチュアート ツイードパーティ', NULL, '2016圣诞', 'HUAZHUANGPIN', 0000004000, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (178, 'デスティニー クローゼット', NULL, '2015圣诞', NULL, 0000004000, NULL, 120, NULL, NULL, 0, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (179, 'ジルスチュアート プリマグレース', NULL, '2014圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (180, 'ジルスチュアート ロイヤル＆アーバンプリンセス', NULL, '2019圣诞', NULL, 0000004000, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (181, 'ジルスチュアート ブラックチュール', NULL, '2019圣诞', 'HUAZHUANGPIN', 0000004000, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (188, 'kanebo 1999', NULL, '1999', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (189, 'kanebo 1998', NULL, '1998', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (190, 'kanebo 1997', NULL, '1997', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (191, 'kanebo 1996', NULL, '1996', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (192, 'kanebo 1991', NULL, '1991', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (193, 'kanebo 1992', NULL, '1992', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (194, 'kanebo 1993', NULL, '1993', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (195, 'レディ ディオール', NULL, 'lady', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', '');
INSERT INTO `mercari_search_condition` VALUES (197, 'セレブレーション コレクション アイ パレット', NULL, '圣诞蝴蝶结眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (198, 'ミッツァ パレット セット', NULL, '丛林', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (199, 'インプレッション キュイール', NULL, '鳄鱼眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (200, '資生堂 バスパウダー', NULL, '沐浴粉', NULL, NULL, NULL, 30, NULL, NULL, 1, '资生堂', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (201, 'スノー フレッシュ ブラッシュ', 'Dior snow fresh cheeks', '雪精灵', NULL, NULL, NULL, 3, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (202, 'ディオール マイレディー', NULL, '编织腮红', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (203, 'ディオール スノー ヴォワール ドゥ ネージュ ', 'diorsnow lumieres de neige', '雪精灵', NULL, NULL, NULL, 3, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (204, 'インフィニティ ロイヤルフラワーコレクション', NULL, '皇家花卉', NULL, NULL, NULL, 120, NULL, NULL, 1, 'kose', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (205, 'インフィニティロイヤルパウダー', NULL, '皇家花卉', NULL, NULL, NULL, 120, NULL, NULL, 1, 'kose', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (206, 'イヴ・サンローラン パレットブラン', NULL, '夹心高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (207, 'ゲラン マダム ルージ', NULL, '波点腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (208, 'ゲラン パリュール ドゥ ソワール', NULL, '羽毛腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (209, 'ブラッシュ ローズ デシール', NULL, '大嘴唇腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (210, 'ランコム  ポップチーク', NULL, 'pop n腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (211, 'トロピーク ミネラル', NULL, '拉链腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (212, 'サンシェルブ ブロンジング パウダー', NULL, '天使修容', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (213, 'プレシャス キャラット', NULL, '钻石唇彩', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (214, 'デスティニー キューブ', NULL, '迪士尼多面体', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (215, 'ラデュレ  サクラ', NULL, '樱花腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (216, 'リミテッド エディション ローズ ラデュレ', NULL, '限量盅', NULL, NULL, NULL, 5, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (217, 'バーバリー シルバーシマー イルミネイティングパウダー', NULL, '蝴蝶结', NULL, NULL, NULL, 120, NULL, NULL, 1, 'Burberry', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (218, 'フラワーブーケ ハンドミラー', NULL, '花嫁镜', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (219, 'ラデュレ 貝殻', NULL, '贝壳', NULL, NULL, NULL, 5, NULL, NULL, 1, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (220, 'ダブルリング ネイルコレクション', NULL, '花嫁套装', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (221, 'ジルスチュアート クリスマスローズ', NULL, '2007圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (222, 'ラデュレ ブラッシュホルダー', NULL, '刷架', NULL, NULL, NULL, 15, NULL, NULL, 0, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (223, 'ラデュレ ブラシホルダー', NULL, '刷架', NULL, NULL, NULL, 15, NULL, NULL, 0, 'laduree', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (224, 'ジルスチュアート  ハンドミラー', NULL, '手镜', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (225, 'ソーホードゥシャネル', NULL, 'soho', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (226, 'ソーホードゥ シャネル', NULL, 'soho', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (227, 'chantecaille', NULL, '全部', NULL, NULL, NULL, 60, NULL, NULL, 1, 'chantecaille', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (228, 'dior 384', NULL, '384星星眼影', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (229, 'dior 766', NULL, '766字母眼影', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (230, 'dior 080', NULL, '080三个小人腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (231, 'dior 468', NULL, '468羽毛腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (232, 'dior 251', NULL, '251logo斜条纹腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (233, 'dior 861', NULL, '861人在桥上腮红', 'LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (235, 'dior 886', NULL, '886波浪格纹', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (236, 'dior 576', NULL, '576波浪格纹', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (238, 'dior 466', NULL, '466南瓜车', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (239, 'dior 753', NULL, '753豹纹眼影', 'YANXIANBI,YANYING', 0000007000, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (241, 'dior 224', NULL, '224射线', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (242, 'dior 654', NULL, '654射线', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (244, 'dior 066', NULL, '066钱币', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (246, 'dior 597', NULL, '597水波纹', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (248, 'dior 617', NULL, '617星星', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (249, 'dior 696', NULL, '696干枯土地', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (250, 'dior 786', NULL, '786干枯土地', 'YANXIANBI,YANYING', 0000005000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (251, 'dior 759', NULL, '759沙漠', 'YANXIANBI,YANYING', 0000005000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (252, 'dior 699', NULL, '699沙漠', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (253, 'dior 769', NULL, '769千鸟格', 'YANXIANBI,YANYING', 0000007000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (254, 'dior 469', NULL, '469门店', 'YANXIANBI,YANYING', 0000004000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (255, 'エレガンス　トレジュール　リップカラー', NULL, '宝石唇膏', NULL, NULL, NULL, 60, NULL, NULL, 1, 'elegance', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (256, 'セーラームーン チークブラシ ', NULL, '腮红刷', NULL, NULL, NULL, 120, NULL, NULL, 1, '美战', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (257, 'セーラームーン', NULL, '腮红', 'LIANJIA', NULL, NULL, 120, NULL, NULL, 1, '美战', '');
INSERT INTO `mercari_search_condition` VALUES (258, 'ジバンシー プードル グロウ', NULL, '花朵粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'givenchy', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (259, 'ミックスフェイスパウダーコンパクト', NULL, '圣诞粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (260, 'シャネル 限定', NULL, '限定粉饼', 'XIANGFEN', NULL, NULL, 5, NULL, NULL, 0, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (261, 'シャネル 限定', NULL, '限定彩妆', 'HUAZHUANGPINALL', NULL, NULL, 5, NULL, NULL, 0, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (262, 'dior 限定', NULL, '限定粉饼', 'XIANGFEN', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (263, 'dior 限定', NULL, '限定彩妆', 'HUAZHUANGPINALL', NULL, NULL, 10, NULL, NULL, 0, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (264, 'estee', NULL, '粉饼', 'XIANGFEN', NULL, NULL, 60, NULL, NULL, 1, 'estee', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (265, 'estee ライトスノーフェースパウダー', NULL, '雪花粉饼', NULL, NULL, NULL, 120, NULL, NULL, 1, 'estee', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (266, 'dolce', NULL, '粉饼', 'XIANGFEN', NULL, NULL, 60, NULL, NULL, 0, 'dolce', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (267, 'dolce', NULL, '腮红', 'LIANJIA,LIANSE', NULL, NULL, 60, NULL, NULL, 0, 'dolce', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (268, 'ミラノコレクション', NULL, '天使粉饼', 'HUAZHUANGPIN', 0000005000, 0000001500, 5, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (272, 'dior love', NULL, 'love', 'HUAZHUANGPINALL', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (273, 'dior tailleur', NULL, 'tailleur', 'HUAZHUANGPINALL', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (274, 'ディオールスキンヌード グロウ サマー パウダー', NULL, '编织粉饼', 'HUAZHUANGPINALL', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (275, 'ルミエールスクルテドゥ シャネル', NULL, '雕花粉饼', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (276, 'ミラノコレクション 2000', NULL, '2000', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (277, 'kanebo', NULL, '天使粉饼', 'XIANGFEN,LIANSE', 0000005500, NULL, 30, NULL, NULL, 0, 'kanebo', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (278, 'GUERLAIN ブラッシュ エクラ', NULL, '齿轮腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (279, 'guerlain', NULL, '腮红粉饼', 'XIANGFEN,LIANJIA,LIANSE', NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (280, 'エフェクト ヌード ハイライト パレット', NULL, '生姜高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'amarni', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (281, 'クルーエル ガーデニア パウダー', NULL, '花朵高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (282, 'ゲラン プードル ドゥ ソワ', NULL, '风车高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (283, 'メテオリット プードル ペルル', NULL, '珠珠高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (284, 'エレガンス ファビュラス ルージュ', NULL, '唇膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'elegance', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (285, 'シャネル レ キャトル オンブル ルミエール ナチュレル ', NULL, 'logo眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (286, 'シャネル デュオ ブロンズ エ ルミエール', NULL, 'logo隔断粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (287, 'RMK フェイスコレクティングカラー', NULL, '爱丽丝', NULL, NULL, NULL, 60, NULL, NULL, 1, 'rmk', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (288, 'rmk  スプリングサーカス', NULL, '马戏团', NULL, NULL, NULL, 60, NULL, NULL, 1, 'rmk', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (289, 'ポール&ジョー ドラえもん', NULL, '哆啦A梦', NULL, NULL, NULL, 60, NULL, NULL, 0, 'paul', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (290, 'Paul & JOE マット プレスト パウダー', NULL, '猫咪剪影粉饼', NULL, NULL, NULL, 60, NULL, NULL, 0, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (291, 'ポールアンドジョー プレストパウダー T001', NULL, '猫头鹰粉饼', NULL, NULL, NULL, 60, NULL, NULL, 0, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (292, 'paul', NULL, '粉饼', 'XIANGFEN,LIANSE', NULL, NULL, 120, NULL, NULL, 1, 'paul', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (293, 'paul アニバーサリー メイクアップ コレクション', NULL, '十周年六个猫咪礼盒', NULL, NULL, NULL, 120, NULL, NULL, 0, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (294, 'プリセ ルミエール ドゥ シャネル', NULL, '折纸高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (295, 'フェイスパレット モンパリクチュール', NULL, '蝴蝶结粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (297, 'DIOR 伊勢丹', NULL, '伊势丹', 'DIZHUANGALL,LIANJIA,LIANSE', NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (298, 'dior 485', NULL, '大圆环腮红', 'LIANJIA,LIANSE', 0000003000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (299, 'paul cs084', NULL, '紫马戏团', NULL, NULL, NULL, 30, NULL, NULL, 1, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (300, 'paul cs083', NULL, '绿马戏团', NULL, NULL, NULL, 30, NULL, NULL, 1, 'paul', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (301, 'jill 103', NULL, '花环腮红', 'LIANJIA,LIANSE', NULL, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (302, 'jill 105', NULL, '星星腮红', 'LIANJIA,LIANSE', NULL, NULL, 30, NULL, NULL, 1, 'jill', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (303, 'レ タンドゥル ドゥ シャネル', NULL, '四色条纹蓝粉款', NULL, NULL, NULL, 30, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (304, 'clarins', NULL, '粉饼', 'XIANGFEN,LIANJIA,LIANSE', NULL, NULL, 120, NULL, NULL, 1, 'clarins', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (305, 'kanebo 2010', NULL, '2010', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (306, 'kanebo 2011', NULL, '2011', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (307, 'kanebo 2012', NULL, '2012', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (308, 'kanebo 2013', NULL, '2013', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (309, 'kanebo 2014', NULL, '2014', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (310, 'kanebo 2015', NULL, '2015', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (311, 'kanebo 2016', NULL, '2016', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (312, 'kanebo 2017', NULL, '2017', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (313, 'kanebo 2018', NULL, '2018', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (314, 'kanebo 2019', NULL, '2019', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (315, 'kanebo 2020', NULL, '2020', 'HUAZHUANGPIN', 0000006500, 0000001500, 60, NULL, NULL, 1, 'kanebo', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (316, 'dior 509', NULL, '509海草', 'HUAZHUANGPIN', 0000007000, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (317, 'dior 889', NULL, '889倒影', 'YANXIANBI,YANYING', 0000003500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (318, 'dior 659', NULL, '659倒影', 'YANXIANBI,YANYING', 0000003500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (319, 'dior 779', NULL, '779竖纹', 'YANXIANBI,YANYING', 0000003500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (320, 'dior 479', NULL, '479竖纹', 'YANXIANBI,YANYING', 0000003500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (321, 'dior 589', NULL, '589圣诞', 'YANXIANBI,YANYING', 0000004000, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (322, 'dior 359', NULL, '359圣诞', 'YANXIANBI,YANYING', 0000004000, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (323, 'dior 601', NULL, '601房子腮红', '', 0000003500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (324, 'シャネル プードゥル スカルプタントゥ', NULL, '小logo棕白粉饼', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (325, 'サンキス リボン アーモニー ドゥ ブラッシュ', NULL, '格纹丝带红棕', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (326, 'プードゥル インプレッション ドゥ シャネル', NULL, 'impression', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (328, 'givenchy 333', NULL, '333', 'HUAZHUANGPIN', 0000001500, NULL, 30, NULL, NULL, 1, 'givenchy', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (329, 'レ ベージュ パレット ルガール インテンス', NULL, '五色眼影', 'HUAZHUANGPIN', 0000005500, NULL, 30, NULL, NULL, 1, 'chanel', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (330, 'ECLATS COSMIQUES', NULL, '星球唇膏', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (331, 'ミネラル ヌード　グロウ パウダー ブルーミングガーデン', NULL, '樱花', 'HUAZHUANGPIN', 0000004500, NULL, 20, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (332, 'ムーシュドボーテ', NULL, 'mouche', 'HUAZHUANGPIN', NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (333, 'POUDRE SIGNEE DE CHANEL ', NULL, '米色印记', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (334, 'dior ファーストライト', NULL, '雪精灵', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (335, 'ディオール スキンヌード グロウパウダー', NULL, '雪精灵/新nude', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (336, 'chanel プードゥル ルミエール', NULL, '小logo蓝白粉饼', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 0, 'chanel', 'QUANXIN');
INSERT INTO `mercari_search_condition` VALUES (338, 'シャネル アイシャドウ マーブル', NULL, '星球眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (339, 'レベージュ プードゥル ベルミン イルミナトゥリス', NULL, '蚊香', NULL, NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (340, 'アンフィニモン シャネル', NULL, 'infinite', NULL, NULL, NULL, 10, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (341, 'ルミエーエルダルティフィス', NULL, '粉珠片', NULL, NULL, NULL, 20, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (342, 'ディシラ　ローズクチュール', NULL, '玫瑰蜜粉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dicila', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (343, 'シャネル オンブル フルリ デリカテス', NULL, '花朵眼影', NULL, NULL, NULL, 30, NULL, NULL, 1, 'chanel', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (344, 'ハイライター ローズ エタンセル', NULL, '玫瑰高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (345, 'プードル エレファン タン', NULL, '大象', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (346, 'ブラッシュ ハイライター', NULL, '长方玫瑰高光', NULL, NULL, NULL, 30, NULL, NULL, 0, 'lancome', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (347, 'パレット エスプリ クチュール', NULL, '人像眼影', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (348, 'パレットYメール', NULL, '邮戳粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (349, 'コンパクト パウダー ラブ コレクション', NULL, '爱心粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (350, 'パレット フォーヴ', NULL, '豹纹眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (351, 'パレット アンテンポレル', NULL, 'label', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (352, 'スノー チェリー ブルーム パウダー', NULL, '条纹樱花', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (353, 'オピウム コレクター パレット', NULL, '荷花', NULL, NULL, NULL, 10, NULL, NULL, 1, 'ysl', NULL);

SET FOREIGN_KEY_CHECKS = 1;
