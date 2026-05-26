-- @cosme 产品关联字段（通过 cosme 页面添加的关键字）
ALTER TABLE `mercari_search_condition`
    ADD COLUMN `cosme_product_id` varchar(20) NULL DEFAULT NULL COMMENT '@cosme 产品ID' AFTER `exclude_keyword`,
    ADD COLUMN `cosme_product_url` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品详情页' AFTER `cosme_product_id`,
    ADD COLUMN `cosme_product_name` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品名称' AFTER `cosme_product_url`;

ALTER TABLE `rakuten_search_condition`
    ADD COLUMN `cosme_product_id` varchar(20) NULL DEFAULT NULL COMMENT '@cosme 产品ID' AFTER `exclude_keyword`,
    ADD COLUMN `cosme_product_url` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品详情页' AFTER `cosme_product_id`,
    ADD COLUMN `cosme_product_name` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品名称' AFTER `cosme_product_url`;

ALTER TABLE `yahoo_search_condition`
    ADD COLUMN `cosme_product_id` varchar(20) NULL DEFAULT NULL COMMENT '@cosme 产品ID' AFTER `exclude_keyword`,
    ADD COLUMN `cosme_product_url` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品详情页' AFTER `cosme_product_id`,
    ADD COLUMN `cosme_product_name` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品名称' AFTER `cosme_product_url`;
