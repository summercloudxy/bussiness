package com.xy.bussiness.mercari.mybean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@TableName("mercari_search_condition")
public class MercariSearchCondition {
    private Integer id;
    private String keyword;
    private String enKeyword;
    private String description;
    private String searchCategory;
    private Integer priceMax;
    private Integer priceMin;
    private Integer duration;
    private Date startTime;
    private Date endTime;
    private String brand;
    private boolean enable;
    private String itemCondition;
    @TableField(exist = false)
    private List<String> conditionList = new ArrayList<>();
    @TableField(exist = false)
    private List<String> categoryList = new ArrayList<>();
    private String excludeKeyword;
    @TableField(exist = false)
    private List<String> excludeKeywordList = new ArrayList<>();
}
