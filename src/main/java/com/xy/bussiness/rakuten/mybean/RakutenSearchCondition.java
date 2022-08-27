package com.xy.bussiness.rakuten.mybean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("rakuten_search_condition")
public class RakutenSearchCondition {
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
    private Integer maxPageNum;
    private String status;
}
