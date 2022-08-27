package com.xy.bussiness.mercari.mybean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mercari_search_condition")
public class MercariSearchCondition {
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
}
