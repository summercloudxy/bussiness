ALTER TABLE `mercari_search_condition`
    ADD COLUMN `cosme_product_image` varchar(255) NULL DEFAULT NULL COMMENT '@cosme 产品图片' AFTER `cosme_product_name`;
