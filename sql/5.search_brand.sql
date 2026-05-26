-- 搜索品牌表：英文 slug 为主键，关联煤炉关键字库与 @cosme 快捷搜索
DROP TABLE IF EXISTS `search_brand`;
CREATE TABLE `search_brand`
(
    `name_en`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '英文品牌名/主键，对应 mercari_search_condition.brand',
    `name_ja`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日文品牌名',
    `mercari_brand_id` int NULL DEFAULT NULL COMMENT '煤炉品牌ID，见 api.mercari.jp/master/v2/datasets/item_brands',
    `cosme_keyword`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '@cosme 搜索关键字',
    PRIMARY KEY (`name_en`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '搜索品牌';

INSERT INTO `search_brand` (`name_en`, `name_ja`, `cosme_keyword`) VALUES
('dior', 'ディオール', 'dior'),
('chanel', 'シャネル', 'chanel'),
('ysl', 'イヴ・サンローラン', 'ysl'),
('lancome', 'ランコム', 'lancome'),
('guerlain', 'ゲラン', 'guerlain'),
('givenchy', 'ジバンシイ', 'givenchy'),
('elegance', 'エレガンス', 'elegance'),
('laduree', 'ラデュレ', 'laduree'),
('jill', 'ジルスチュアート', 'jill'),
('kanebo', 'カネボウ', 'kanebo'),
('kose', 'コーセー', 'kose'),
('decorte', 'コスメデコルテ', 'decorte'),
('cpb', 'クレ・ド・ポー ボーテ', 'cpb'),
('albion', 'アルビオン', 'albion'),
('mac', 'MAC', 'mac'),
('clarins', 'クラランス', 'clarins'),
('maquillage', 'マキアージュ', 'maquillage'),
('estee', 'エスティ ローダー', 'estee'),
('dolce', 'ドルチェ＆ガッバーナ', 'dolce'),
('Burberry', 'バーバリー', 'Burberry'),
('armani', 'アルマーニ', 'armani'),
('chantecaille', 'シャンテカイユ', 'chantecaille'),
('paul', 'ポール＆ジョー', 'paul'),
('rmk', 'RMK', 'rmk'),
('资生堂', '資生堂', '資生堂');
