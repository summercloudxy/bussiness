-- 修正 amarni 拼写错误，统一为 armani
UPDATE `mercari_search_condition` SET `brand` = 'armani' WHERE `brand` = 'amarni';
UPDATE `rakuten_search_condition` SET `brand` = 'armani' WHERE `brand` = 'amarni';
UPDATE `yahoo_search_condition` SET `brand` = 'armani' WHERE `brand` = 'amarni';
DELETE FROM `search_brand` WHERE `name_en` = 'amarni';
