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

 Date: 15/09/2022 17:23:23
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
) ENGINE = InnoDB AUTO_INCREMENT = 268 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mercari_search_condition
-- ----------------------------
INSERT INTO `mercari_search_condition` VALUES (2, 'シャネル レティサージュ ラメ', '鸟笼', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (3, 'シャネル メイクアップカラー', '珠珠', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (4, 'レ ペルル ドゥ シャネル', '珠珠', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (5, 'シャネル ルミエールダルティフィス', '珠片', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (6, 'プードゥル ティセ フェイスパウダー', 'tissee', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (7, 'ジーンズ ドゥ シャネル ', '牛仔', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (8, 'シャネル イレールグロス トランスパラン', '晃动logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (9, 'リップグロス イレール', '晃动logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (10, 'アンタンス ラディアンス', '晃动logo', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (11, 'コロマンデル ドゥ シャネル', '东方屏风', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (12, 'シャネル プルミエール フルール', '东方屏风', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (13, 'シャネル ペルル エ ファンテジー', '双色珠链', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (14, 'シャネル ピンク ラメ', '毛呢', 'HUAZHUANGPINALL', NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (16, 'シャネル  ソーシャネル', 'so chanel', 'HUAZHUANGPIN', NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (17, 'シャネル レーヴ ドリヤン', '星星四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (18, 'シャネル クワトル マーブル ドゥ シャネル タンドゥル', '星球四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (19, 'クワトル マーブル ドゥ シャネル', '星球四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (20, 'アンプラント ドゥ シャネル', '沙漠logo', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (21, 'シャネル トゥロワ リンニュ ドゥ', '黑眉粉', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (22, 'グワッシュ ドゥ シャネル', '调色盘', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (23, 'シャネル リュバン ペルレ', '九宫格粉饼', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (25, 'シャネル プードゥルシニ', '米色印记', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (26, 'ルート デ ザンド ドゥ シャネル', '孟买', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (27, 'ソーホー ドゥ シャネル', 'soho', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (28, 'シャネル フェイスカラー ラッキー ストライプ', 'lucky', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (29, 'ルミエール スクルテ ドゥ シャネル', '雕花粉饼', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (30, 'シャネル ジャルダン ドゥ カメリア', '脑花粉饼', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (31, 'シャネルロンドンマッドネス', '英国伦敦', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (33, 'プードゥルインプレッションドゥシャネル', 'Poudre Impressions ', NULL, NULL, NULL, 1, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (34, 'オンブル ティセ ベージュ', '粉色渐变高光', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (35, 'シャネル オンブルティセ', '粉色渐变高光', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (36, 'ルミエールダルティフィス', '钱币', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (37, 'プードゥルサテン ドゥ シャネル', '缎带', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (38, 'シャネル プードゥルティセ', 'logo带闪腮红', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (39, 'シャネル カメリア ドゥ プルム プラティン', '羽毛高光', NULL, NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (40, 'シャネル デュオドゥカメリア', '双色高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (41, 'シャネル ヌワール ラメ', '星球唇膏', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (42, 'シャネル レーヴ ドゥ カメリア', '山茶花高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (43, 'シャネル ルミエール デテ', '山茶花高光', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (44, 'シャネル ドンテル プレシューズ', '蕾丝', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (45, 'ムーシュドゥボーテ', 'mouche', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (46, 'シャネル ルールセレスト', '花朵三色', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (47, 'ブラッシュ オリゾン ドゥ シャネル', '粉色白色条纹', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (48, 'ルガール シニエ ドゥ シャネル', '拜占庭四色眼影', NULL, NULL, NULL, 2, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (49, 'ディオール プリンセスリング', '戒指', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (50, 'フルール ドゥ ヴァン パレット', '蒲公英', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (51, 'ディオール フラワーブロッサム', '花园', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (52, 'ディオール クリスタルボレアル', '凡尔赛', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (53, 'リップグロス ペンダント スワロスキー', '施华洛世奇', NULL, NULL, NULL, 2, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (54, 'Diorプリティチャーム', '心形唇彩', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (55, 'スノー イリディセント パウダー', '雪花粉饼', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (56, 'ディオール ディオリッシム', '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (57, 'ディオール ガーデンクラッチ', '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (58, 'Dior ミノディエール', '手包', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (59, 'ディオール ホリデイ コレクション', 'holiday', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (60, 'プードリエ ダンテル', '蕾丝', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (61, 'ディオールプレイ', '骰子', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (62, 'ミラノコレクション 21st', '21st天使粉', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (63, 'ミラノコレクション 2001', '2001', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (64, 'ミラノコレクション 2002', '2002', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (65, 'ミラノコレクション 2003', '2003', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (66, 'ミラノコレクション 2004', '2004', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (67, 'ミラノコレクション 2005', '2005', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (68, 'ミラノコレクション 2006', '2006', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (69, 'ミラノコレクション 2007', '2007', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (70, 'ミラノコレクション 2008', '2008', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (71, 'ミラノコレクション 2009', '2009', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (72, 'ミラノコレクション 2010', '2010', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (73, 'ランコム スパークリングシェルブ', '天使', NULL, NULL, NULL, 10, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (75, 'ゲラン ラディアント シャドウ', '眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'guerlain', NULL);
INSERT INTO `mercari_search_condition` VALUES (76, 'マルセル ワンダース', '蜜粉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'decorte', NULL);
INSERT INTO `mercari_search_condition` VALUES (77, 'アルビオン レガァーメ', '宝格丽蜜粉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'albion', NULL);
INSERT INTO `mercari_search_condition` VALUES (78, 'YSL ジュエルリップスティック', '宝石口红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (79, 'ヴィンテージ イブサンローラン ジュエル リップステック', '宝石口红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (80, 'YSL ジュエルパウダー', '宝石粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (81, 'サンローランジュエルコンパクト ブローチ', '宝石粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (82, 'ゲラン ラディアント バタフライ', '蝴蝶夫人', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', NULL);
INSERT INTO `mercari_search_condition` VALUES (83, 'クレ・ド・ポー ボーテ パレットヴィンテージ', '高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'cpb', NULL);
INSERT INTO `mercari_search_condition` VALUES (84, '資生堂 七色粉白粉', '七色粉白粉', NULL, NULL, NULL, 30, NULL, NULL, 1, '资生堂', NULL);
INSERT INTO `mercari_search_condition` VALUES (85, 'パレット パリジャンナイト ブラッシュ', '爱心腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (86, 'ラデュレ 6TH', '鸟笼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (87, 'マキアージュ スノービューティーII', '雪花粉饼2015', NULL, NULL, NULL, 10, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (88, '資生堂 レ プードリエ オン ドゥ', '人像壳子腮红眼影', NULL, NULL, NULL, 60, NULL, NULL, 1, '资生堂', NULL);
INSERT INTO `mercari_search_condition` VALUES (89, 'クレドポー ボーテ パレット ド・ミル カラ', '高光同款唇膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'cpb', NULL);
INSERT INTO `mercari_search_condition` VALUES (90, 'パウダーセレブレーション', '粉末庆典', NULL, NULL, NULL, 60, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (91, 'ラ・プードル ルイスロント', '花朵粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, '资生堂', NULL);
INSERT INTO `mercari_search_condition` VALUES (92, 'ジルスチュアート 練り香水', '固体香膏', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (93, 'ジルスチュアート ダイアモンドデュウ リップカラー', '戒指', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (94, 'ヴィンテージジュエル コンパクトミラー', '戒指同款镜子', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (95, 'ローズリベルテ', '黄色玫瑰', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (96, 'バタフライフィーバー', '蝴蝶', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (97, 'メゾン ランコム', 'maison', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (98, ' スノービューティー  2014', '雪花粉饼2014', NULL, NULL, NULL, 30, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (99, 'スノービューティー  初代', '雪花粉饼2014', NULL, NULL, NULL, 30, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (100, ' スノービューティー  白雪姫', '雪花粉饼白雪', NULL, NULL, NULL, 30, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (101, 'クレ・ド・ポー ボーテ プードルコンパクト', '粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, 'cpb', NULL);
INSERT INTO `mercari_search_condition` VALUES (102, 'クワトル ブトン ドゥ シャネル アイシャドウ', '彩色logo眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (103, 'QUATUOR BOUTONS DE CHANEL', '彩色logo眼影', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (104, 'ディオールディオール スノウ', '雪精灵', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (105, 'カネボウ ベルミュージアム コンパクト', '93年悬赏品', NULL, NULL, NULL, 60, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (106, 'イヴ・サンローラン ラブコレクション', '爱心', NULL, NULL, NULL, 120, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (107, '資生堂 プードル コーディアル', '陶瓷蜜粉', NULL, NULL, NULL, 60, NULL, NULL, 1, '资生堂', NULL);
INSERT INTO `mercari_search_condition` VALUES (108, 'プードルクルーズエキゾチック', '异国情调粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, 'givenchy', NULL);
INSERT INTO `mercari_search_condition` VALUES (109, 'ラデュレ ポット ローズ', '花瓣腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (110, 'ラデュレ リミテッドエディション', '限量腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (111, 'ラデュレ 花びら', '花瓣腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (113, 'Dior グランバル 001', '金灿', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (114, 'Dior NIGHT DIAMOND', 'night diamond', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (115, 'バーバリーファーストラブ', 'first love', NULL, NULL, NULL, 30, NULL, NULL, 1, 'Burberry', NULL);
INSERT INTO `mercari_search_condition` VALUES (116, 'メテオリットヴォワイヤージュ', '蝴蝶/蜜蜂', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', NULL);
INSERT INTO `mercari_search_condition` VALUES (117, 'ディオール スキンヌード エアーパウダー コンパクト', '老版nude', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (118, 'ヌード トランザット パウダー', '编织nude', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (119, 'ディオリフィック パウダー', 'diorific', NULL, NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (120, 'ルナソル パーティアイズ 2014', '扑克牌', NULL, NULL, NULL, 60, NULL, NULL, 1, 'lunasol', NULL);
INSERT INTO `mercari_search_condition` VALUES (121, 'クリスチャン ディオール トリアノン パレット', '蝴蝶结手包', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (122, 'Dior ディオール シェリー ボウ パレット ', '蝴蝶结手包', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (123, 'ラデュレ アイシャドウ', '帽子眼影', NULL, NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (124, 'Dior アイシャドウ ジャズクラブ', 'jazzclub', NULL, NULL, NULL, 60, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (125, 'エスティ ローダー ワールドトラベルリップケース', '世界旅行', NULL, NULL, NULL, 240, NULL, NULL, 1, 'estee', NULL);
INSERT INTO `mercari_search_condition` VALUES (126, 'estee world', '世界旅行', NULL, NULL, NULL, 240, NULL, NULL, 1, 'estee', NULL);
INSERT INTO `mercari_search_condition` VALUES (127, 'バーバリー ゴールドグロウ フレグランス', '蝴蝶结', NULL, NULL, NULL, 120, NULL, NULL, 1, 'Burberry', NULL);
INSERT INTO `mercari_search_condition` VALUES (128, 'ディオール アンセルムライル パレット', '蓝色丛林眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (129, 'ディオール ナイト ダイヤモンド', 'night diamond', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (130, 'イヴサンローラン ユアシークレット ラグジュアリー', '手链唇膏香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (131, 'ディオール ブラッシュハーモニー', 'harmony', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (132, 'ポール ジョー シマリングプレストパウダー', '蝴蝶', NULL, NULL, NULL, 30, NULL, NULL, 1, 'paul', NULL);
INSERT INTO `mercari_search_condition` VALUES (133, 'クリスチャンディオール オリガミ', '折纸', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (134, 'マイ レディー ブラッシュ', '编织腮红', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (135, 'Dior ビューティコンフィデンシャル', '信封', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (136, 'ランコム ルーシアイズ', '贝壳', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (137, 'ランコム ルーシーアイズ', '贝壳', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (138, 'ラデュレ プレストフェイスパウダー', '蛋糕', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (139, 'ランコム パレット ブラゾン', 'logo款腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (140, 'dior 441', '441', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (141, 'dior 841', '841', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (142, 'dior 791', '791', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (143, 'dior 059', '059蕾丝', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (144, 'dior 743', '743蕾丝', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (145, 'dior 724', '724', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (146, 'dior 451', '451繁花', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (147, 'dior 031', '031繁花', 'YANXIANBI,YANYING', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (148, 'dior 776', '776钱币眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (149, 'dior 717', '717', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (150, ' エレガンス ファンタムアイズ', '眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'elegance', NULL);
INSERT INTO `mercari_search_condition` VALUES (151, 'オンブル ラメ ドゥ シャネル', '麦穗', NULL, NULL, NULL, 5, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (152, 'dior 671', '671', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (153, 'dior 844', '844', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (154, 'dior 442', '442', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (155, 'dior 536', '536', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (156, 'dior 946', '946', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (157, 'スノービューティー Ⅱ', '心机粉饼2015', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'maquillage', NULL);
INSERT INTO `mercari_search_condition` VALUES (158, 'ナイトジュエルソリッドパフューム', 'night固体香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (159, 'ジルスチュアート クリスタルブルーム ソリッドパフューム', '花朵固体香膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (160, 'RMK トランスルーセント チークス&アイズ', '小花花', NULL, NULL, NULL, 120, NULL, NULL, 1, 'rmk', NULL);
INSERT INTO `mercari_search_condition` VALUES (161, 'ESTEE LAUDER ワールド', 'world travel', NULL, NULL, NULL, 240, NULL, NULL, 1, 'estee', NULL);
INSERT INTO `mercari_search_condition` VALUES (162, 'ランコム　ロータススプレンダー', '莲花', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (163, 'ジルスチュアート クリスマス', '圣诞', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (164, 'ラデュレ ポット', '盅', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (165, 'dior ゴルメット', '手链唇膏', NULL, NULL, NULL, 30, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (166, 'シャネル ブロンジングパウダー', 'logo修容粉饼', NULL, NULL, NULL, 10, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (167, 'フェイスパレット モン パリフローラル', '蝴蝶结粉饼', NULL, NULL, NULL, 120, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (168, 'イヴサンローラン  モンパリ', '蝴蝶结粉饼', 'XIANGFEN', NULL, NULL, 120, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (169, 'スウィートネスコレクション', '2009圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (171, 'ジルスチュアート マイディアストロベリー', '2013圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (172, 'ジルスチュアート スティーリングハート', '2012圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (173, 'ジルスチュアート フェアリーガーデン', '2011圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (174, 'ジルスチュアート シークレットティーズ', '2010圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (175, 'ジルスチュアート ホワイトラブストーリー', '2018圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (176, 'ジルスチュアート パジャマパーティ', '2017圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (177, 'ジルスチュアート ツイードパーティ', '2016圣诞', 'HUAZHUANGPIN', NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (178, 'デスティニー クローゼット', '2015圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (179, 'ジルスチュアート プリマグレース', '2014圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (180, 'ジルスチュアート ロイヤル＆アーバンプリンセス', '2019圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (181, 'ジルスチュアート ブラックチュール', '2019圣诞', 'HUAZHUANGPIN', NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (182, 'ミラノコレクション 2011', '2011', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (183, 'ミラノコレクション 2012', '2012', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (184, 'ミラノコレクション 2013', '2013', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (185, 'ミラノコレクション 2014', '2014', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (186, 'ミラノコレクション 2015', '2015', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (187, 'ミラノコレクション 2016', '2016', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (188, 'ミラノコレクション 1999', '1999', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (189, 'ミラノコレクション 1998', '1998', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (190, 'ミラノコレクション 1997', '1997', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (191, 'ミラノコレクション 1996', '1996', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (192, 'ミラノコレクション 1991', '1991', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (193, 'ミラノコレクション 1992', '1992', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (194, 'ミラノコレクション 1993', '1993', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kanebo', NULL);
INSERT INTO `mercari_search_condition` VALUES (195, 'レディ ディオール', 'lady', 'HUAZHUANGPIN', NULL, NULL, 5, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (197, 'セレブレーション コレクション アイ パレット', '圣诞蝴蝶结眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (198, 'ミッツァ パレット セット', '丛林', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (199, 'インプレッション キュイール', '鳄鱼眼影', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (200, '資生堂 バスパウダー', '沐浴粉', NULL, NULL, NULL, 30, NULL, NULL, 1, '资生堂', NULL);
INSERT INTO `mercari_search_condition` VALUES (201, 'スノー フレッシュ ブラッシュ', '雪精灵', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (202, 'ディオール マイレディー', '编织腮红', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (203, 'ディオール スノー ヴォワール ドゥ ネージュ ', '雪精灵', NULL, NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (204, 'インフィニティ ロイヤルフラワーコレクション', '皇家花卉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kose', NULL);
INSERT INTO `mercari_search_condition` VALUES (205, 'インフィニティロイヤルパウダー', '皇家花卉', NULL, NULL, NULL, 30, NULL, NULL, 1, 'kose', NULL);
INSERT INTO `mercari_search_condition` VALUES (206, 'イヴ・サンローラン パレットブラン', '夹心高光', NULL, NULL, NULL, 30, NULL, NULL, 1, 'ysl', NULL);
INSERT INTO `mercari_search_condition` VALUES (207, 'ゲラン マダム ルージ', '波点腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', NULL);
INSERT INTO `mercari_search_condition` VALUES (208, 'ゲラン パリュール ドゥ ソワール', '羽毛腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'guerlain', NULL);
INSERT INTO `mercari_search_condition` VALUES (209, 'ブラッシュ ローズ デシール', '大嘴唇腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (210, 'ランコム  ポップチーク', 'pop n腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (211, 'トロピーク ミネラル', '拉链腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (212, 'サンシェルブ ブロンジング パウダー', '天使修容', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (213, 'プレシャス キャラット', '钻石唇彩', 'HUAZHUANGPIN', NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (214, 'デスティニー キューブ', '迪士尼多面体', NULL, NULL, NULL, 30, NULL, NULL, 1, 'lancome', NULL);
INSERT INTO `mercari_search_condition` VALUES (215, 'ラデュレ  サクラ', '樱花腮红', NULL, NULL, NULL, 30, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (216, 'リミテッド エディション ローズ ラデュレ', '限量盅', NULL, NULL, NULL, 5, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (217, 'バーバリー シルバーシマー イルミネイティングパウダー', '蝴蝶结', NULL, NULL, NULL, 120, NULL, NULL, 1, 'Burberry', NULL);
INSERT INTO `mercari_search_condition` VALUES (218, 'フラワーブーケ ハンドミラー', '花嫁镜', NULL, NULL, NULL, 30, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (219, 'ラデュレ 貝殻', '贝壳', NULL, NULL, NULL, 5, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (220, 'ダブルリング ネイルコレクション', '花嫁套装', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (221, 'ジルスチュアート クリスマスローズ', '2007圣诞', NULL, NULL, NULL, 120, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (222, 'ラデュレ ブラッシュホルダー', '刷架', NULL, NULL, NULL, 15, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (223, 'ラデュレ ブラシホルダー', '刷架', NULL, NULL, NULL, 15, NULL, NULL, 1, 'laduree', NULL);
INSERT INTO `mercari_search_condition` VALUES (224, 'ジルスチュアート  ハンドミラー', '手镜', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (225, 'ソーホードゥシャネル', 'soho', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (226, 'ソーホードゥ シャネル', 'soho', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (227, 'chantecaille', '全部', NULL, NULL, NULL, 60, NULL, NULL, 1, 'chantecaille', NULL);
INSERT INTO `mercari_search_condition` VALUES (228, 'dior 384', '384星星眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (229, 'dior 766', '766字母眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (230, 'dior 080', '080三个小人腮红', 'LIANJIA', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (231, 'dior 468', '468羽毛腮红', 'LIANJIA', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (232, 'dior 251', '251logo斜条纹腮红', 'LIANJIA', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (233, 'dior 861', '861人在桥上腮红', 'LIANJIA', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (235, 'dior 886', '886波浪格纹', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (236, 'dior 576', '576波浪格纹', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (238, 'dior 446', '446南瓜车', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (239, 'dior 753', '753豹纹眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (240, 'dior 753', '753豹纹眼影', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (241, 'dior 224', '224射线', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (242, 'dior 654', '654射线', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (244, 'dior 066', '066钱币', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (246, 'dior 597', '597水波纹', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (247, 'dior 597', '597水波纹', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (248, 'dior 617', '617星星', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (249, 'dior 696', '696干枯土地', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (250, 'dior 786', '786干枯土地', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (251, 'dior 759', '759沙漠', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (252, 'dior 699', '699沙漠', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (253, 'dior 769', '769千鸟格', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (254, 'dior 469', '469门店', 'HUAZHUANGPIN', NULL, NULL, 10, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (255, 'エレガンス　トレジュール　リップカラー', '宝石唇膏', NULL, NULL, NULL, 60, NULL, NULL, 1, 'elegance', NULL);
INSERT INTO `mercari_search_condition` VALUES (256, 'セーラームーン チークブラシ ', '腮红刷', NULL, NULL, NULL, 120, NULL, NULL, 1, '美战', NULL);
INSERT INTO `mercari_search_condition` VALUES (257, 'セーラームーン', '腮红', 'LIANJIA', NULL, NULL, 120, NULL, NULL, 1, '美战', '');
INSERT INTO `mercari_search_condition` VALUES (258, 'ジバンシー プードル グロウ', '花朵粉饼', NULL, NULL, NULL, 30, NULL, NULL, 1, 'givenchy', NULL);
INSERT INTO `mercari_search_condition` VALUES (259, 'ミックスフェイスパウダーコンパクト', '圣诞粉饼', NULL, NULL, NULL, 60, NULL, NULL, 1, 'jill', NULL);
INSERT INTO `mercari_search_condition` VALUES (260, 'シャネル 限定', '限定粉饼', 'DIZHUANGALL', NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (261, 'シャネル 限定', '限定彩妆', 'HUAZHUANGPINALL', NULL, NULL, 3, NULL, NULL, 1, 'chanel', NULL);
INSERT INTO `mercari_search_condition` VALUES (262, 'dior 限定', '限定粉饼', 'DIZHUANGALL', NULL, NULL, 3, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (263, 'dior 限定', '限定彩妆', 'HUAZHUANGPINALL', NULL, NULL, 3, NULL, NULL, 1, 'dior', NULL);
INSERT INTO `mercari_search_condition` VALUES (264, 'estee', '粉饼', 'XIANGFEN', NULL, NULL, 60, NULL, NULL, 1, 'estee', NULL);
INSERT INTO `mercari_search_condition` VALUES (265, 'estee ライトスノーフェースパウダー', '雪花粉饼', NULL, NULL, NULL, 120, NULL, NULL, 1, 'estee', NULL);
INSERT INTO `mercari_search_condition` VALUES (266, 'dolce', '粉饼', 'XIANGFEN', NULL, NULL, 60, NULL, NULL, 1, 'dolce', 'QUANXIN,JINQUANXIN');
INSERT INTO `mercari_search_condition` VALUES (267, 'dolce', '腮红', 'LIANJIA,LIANSE', NULL, NULL, 60, NULL, NULL, 1, 'dolce', 'QUANXIN,JINQUANXIN');

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 216 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rakuten_search_condition
-- ----------------------------
INSERT INTO `rakuten_search_condition` VALUES (158, 'chanel', '香奈儿', '424', NULL, NULL, 60, NULL, NULL, 1, 'chanel', NULL, 3, 'new');
INSERT INTO `rakuten_search_condition` VALUES (159, 'dior', '迪奥', '424', NULL, NULL, 60, NULL, NULL, 1, 'dior', NULL, 3, 'new');
INSERT INTO `rakuten_search_condition` VALUES (160, 'マルセル ワンダース', '黛珂蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'decorte', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (161, 'ミラノコレクション', '嘉娜宝', NULL, NULL, NULL, 60, NULL, NULL, 1, 'kanebo', NULL, 2, NULL);
INSERT INTO `rakuten_search_condition` VALUES (162, 'ランコム スパークリングシェルブ', '天使', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (163, '資生堂 七色粉白粉', '七色粉白粉', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (164, 'ラデュレ', '拉杜丽', '10004', NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 3, NULL);
INSERT INTO `rakuten_search_condition` VALUES (165, ' スノービューティー ', '雪花粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'maquillage', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (166, 'ルート デ ザンド', '孟买', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (167, 'シャネル トゥロワ リンニュ ドゥ', '黑眉粉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (168, 'シャネル ロンドンマッドネス', '疯狂伦敦', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (169, 'プードゥルインプレッションドゥシャネル', 'Poudre Impressions ', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (170, 'ムーシュドゥボーテ', 'mouche', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (171, 'ルミエールダルティフィス', '钱币', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (172, 'シャネル レティサージュ ラメ', '鸟笼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (173, 'プードゥル ティセ フェイスパウダー', 'tissee', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (174, 'ソーホー ドゥ シャネル', 'soho', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (175, 'アルビオン レガァーメ', '宝格丽蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'Albion', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (176, 'YSL ジュエルリップスティック', '宝石口红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (177, 'ヴィンテージ イブサンローラン ジュエル リップステック', '宝石口红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (178, 'YSL ジュエルパウダー', '宝石粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (179, 'サンローランジュエルコンパクト、ブローチ', '宝石粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (180, 'バタフライフィーバー', '蝴蝶', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (181, 'メゾン ランコム', 'maison', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (182, '資生堂 プードル コーディアル', '陶瓷蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (183, 'ラ・プードル ルイスロント', '花朵粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (184, 'イヴサンローラン ユアシークレット ラグジュアリー', '手链唇膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (185, 'ディオール ブラッシュハーモニー', 'harmony', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (186, 'オンブル ラメ ドゥ シャネル', '麦穗', NULL, NULL, NULL, 120, NULL, NULL, 1, 'chanel', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (187, ' エレガンス ファンタムアイズ', '幻影眼影', '424', NULL, NULL, 240, NULL, NULL, 1, 'elegance', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (188, 'ディオール プリンセスリング', '戒指', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (189, 'ゲラン ラディアント シャドウ', '眼影', NULL, NULL, NULL, 240, NULL, NULL, 1, 'guerlain', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (190, 'ナイトジュエルソリッドパフューム', 'night香膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (191, 'ジルスチュアート クリスタルブルーム ソリッドパフューム', '花朵香膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (192, 'dior ゴルメット', '手链唇膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (193, 'パウダーセレブレーション', '粉末庆典', NULL, NULL, NULL, 240, NULL, NULL, 1, 'maquillage', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (195, 'カネボウ ベルミュージアム コンパクト', '93年悬赏品', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kanebo', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (196, 'フェイスパレット モン パリフローラル', '蝴蝶结粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (197, 'パレット パリジャンナイト ブラッシュ ルードバビロン', '爱心腮红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (198, 'ジルスチュアート クリスマス', '圣诞', '424', NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (199, 'ジルスチュアート ダイアモンドデュウ リップカラー', '戒指', '424', NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (200, 'インフィニティ ロイヤルフラワーコレクション', '皇家花卉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kose', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (201, 'インフィニティロイヤルパウダー', '皇家花卉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kose', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (202, '資生堂 バスパウダー', '沐浴粉', NULL, NULL, NULL, 120, NULL, NULL, 1, '资生堂', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (203, 'プレシャス キャラット', '钻石唇膏', '424', NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (204, 'laduree  ポット', '盅', NULL, NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (205, 'リミテッド エディション ローズ ラデュレ', '限量盅', NULL, NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (206, 'ラデュレ ブラシホルダー', '刷架', NULL, NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (207, 'ラデュレ ブラッシュホルダー', '刷架', NULL, NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (208, 'エレガンス トレジュール リップカラー', '宝石唇彩', '424', NULL, NULL, 240, NULL, NULL, 1, 'elegance', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (209, 'chantecaille', '全部', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chantecaille', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (210, 'セーラームーン チークブラシ ', '腮红刷', NULL, NULL, NULL, 240, NULL, NULL, 1, '美战', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (211, 'セーラームーン', '腮红', '440', NULL, NULL, 240, NULL, NULL, 1, '美战', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (212, 'ジバンシー プードル グロウ', '花朵粉饼', '424', NULL, NULL, 240, NULL, NULL, 1, 'givenchy', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (213, 'ミックスフェイスパウダーコンパクト', '圣诞粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (214, 'メテオリットヴォワイヤージュ', '蝴蝶/蜜蜂', NULL, NULL, NULL, 240, NULL, NULL, 1, 'guerlain', NULL, 1, NULL);
INSERT INTO `rakuten_search_condition` VALUES (215, 'estee', '粉饼', '429', NULL, NULL, 240, NULL, NULL, 1, 'estee', NULL, 1, NULL);

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
  `page_size` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 202 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yahoo_search_condition
-- ----------------------------
INSERT INTO `yahoo_search_condition` VALUES (158, 'chanel', '香奈儿', '42180', NULL, NULL, 60, NULL, NULL, 1, 'chanel', NULL, 100);
INSERT INTO `yahoo_search_condition` VALUES (159, 'dior', '迪奥', '42180', NULL, NULL, 60, NULL, NULL, 1, 'dior', NULL, 100);
INSERT INTO `yahoo_search_condition` VALUES (160, 'マルセル ワンダース', '黛珂蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'decorte', NULL, 50);
INSERT INTO `yahoo_search_condition` VALUES (161, 'ミラノコレクション', '嘉娜宝', NULL, NULL, NULL, 60, NULL, NULL, 1, 'kanebo', NULL, 100);
INSERT INTO `yahoo_search_condition` VALUES (162, 'ランコム スパークリングシェルブ', '天使', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (163, '資生堂 七色粉白粉', '七色粉白粉', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (164, 'ラデュレ', '拉杜丽', '42180', NULL, NULL, 60, NULL, NULL, 1, 'laduree', NULL, 100);
INSERT INTO `yahoo_search_condition` VALUES (165, ' スノービューティー ', '雪花粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'maquillage', NULL, 50);
INSERT INTO `yahoo_search_condition` VALUES (166, 'ルート デ ザンド', '孟买', '42180', NULL, NULL, 120, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (167, 'シャネル トゥロワ リンニュ ドゥ', '黑眉粉', NULL, NULL, NULL, 120, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (168, 'シャネル ロンドンマッドネス', '疯狂伦敦', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (169, 'プードゥルインプレッションドゥシャネル', 'Poudre Impressions ', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (170, 'ムーシュドゥボーテ', 'mouche', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (171, 'ルミエールダルティフィス', '钱币', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 2);
INSERT INTO `yahoo_search_condition` VALUES (172, 'シャネル レティサージュ ラメ', '鸟笼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (173, 'プードゥル ティセ フェイスパウダー', 'tissee', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (174, 'ソーホー ドゥ シャネル', 'soho', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (175, 'アルビオン レガァーメ', '宝格丽蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'Albion', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (176, 'YSL ジュエルリップスティック', '宝石口红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (177, 'ヴィンテージ イブサンローラン ジュエル リップステック', '宝石口红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (178, 'YSL ジュエルパウダー', '宝石粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (179, 'サンローランジュエルコンパクト、ブローチ', '宝石粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (180, 'バタフライフィーバー', '蝴蝶', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (181, 'メゾン ランコム', 'maison', NULL, NULL, NULL, 240, NULL, NULL, 1, 'lancome', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (182, '資生堂 プードル コーディアル', '陶瓷蜜粉', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (183, 'ラ・プードル ルイスロント', '花朵粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, '资生堂', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (184, 'イヴサンローラン ユアシークレット ラグジュアリー', '手链唇膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (185, 'ディオール ブラッシュハーモニー', 'harmony', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (186, 'オンブル ラメ ドゥ シャネル', '麦穗', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chanel', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (187, ' エレガンス ファンタムアイズ', '幻影眼影', '42180', NULL, NULL, 240, NULL, NULL, 1, 'elegance', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (188, 'ディオール プリンセスリング', '戒指', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (189, 'ゲラン ラディアント シャドウ', '眼影', NULL, NULL, NULL, 240, NULL, NULL, 1, 'guerlain', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (190, 'ナイトジュエルソリッドパフューム', 'night香膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (191, 'ジルスチュアート クリスタルブルーム ソリッドパフューム', '花朵香膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (192, 'dior ゴルメット', '手链唇膏', NULL, NULL, NULL, 240, NULL, NULL, 1, 'dior', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (193, 'カネボウ ベルミュージアム コンパクト', '93年悬赏品', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kanebo', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (194, 'フェイスパレット モン パリフローラル', '蝴蝶结粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (195, 'パレット パリジャンナイト ブラッシュ ルードバビロン', '爱心腮红', NULL, NULL, NULL, 240, NULL, NULL, 1, 'ysl', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (196, 'インフィニティ ロイヤルフラワーコレクション', '皇家花卉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kose', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (197, 'インフィニティロイヤルパウダー', '皇家花卉', NULL, NULL, NULL, 240, NULL, NULL, 1, 'kose', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (198, 'ジルスチュアート ダイアモンドデュウ リップカラー', '戒指', NULL, NULL, NULL, 240, NULL, NULL, 1, 'jill', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (199, 'chantecaille', '全部', NULL, NULL, NULL, 240, NULL, NULL, 1, 'chantecaille', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (200, 'メテオリットヴォワイヤージュ', '蝴蝶/蜜蜂', NULL, NULL, NULL, 240, NULL, NULL, 1, 'guerlain', NULL, 20);
INSERT INTO `yahoo_search_condition` VALUES (201, 'ジバンシー プードル グロウ', '花朵粉饼', NULL, NULL, NULL, 240, NULL, NULL, 1, 'givenchy', NULL, 20);

SET FOREIGN_KEY_CHECKS = 1;
