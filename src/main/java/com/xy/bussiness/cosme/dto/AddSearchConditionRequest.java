package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddSearchConditionRequest {
    private String keyword;
    private String description;
    private String brand;
    private Integer duration;
    private boolean mercari;
    private boolean rakuten;
    private boolean yahoo;
    private List<String> mercariCategories = new ArrayList<>();
    private List<String> mercariConditions = new ArrayList<>();
    private String rakutenSearchCategory;
    private Integer rakutenMaxPageNum;
    private String rakutenStatus;
    private String yahooSearchCategory;
    private Integer yahooPageSize;
    private String excludeKeyword;
    /** 爬虫关键字价格下限（日元），写入已勾选平台的关键字表 */
    private Integer priceMin;
    /** 爬虫关键字价格上限（日元），写入已勾选平台的关键字表 */
    private Integer priceMax;
    private String cosmeProductId;
    private String cosmeProductUrl;
    private String cosmeProductName;
    private String cosmeProductImage;
}
