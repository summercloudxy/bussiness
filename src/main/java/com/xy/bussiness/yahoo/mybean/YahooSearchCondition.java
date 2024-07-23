package com.xy.bussiness.yahoo.mybean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yahoo_search_condition")
public class YahooSearchCondition {
    private Integer id;
    private String keyword;
    private String description;
    private String searchCategory;
    private Integer priceMax;
    private Integer priceMin;
    private Integer duration;
    private Date startTime;
    private Date endTime;
    private String brand;
    private boolean enable;
    private String searchUrl;
    private Integer pageSize;
    private String excludeKeyword;

}
