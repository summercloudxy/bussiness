package com.xy.bussiness.mercari.mybean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("mercari_seller_search_condition")
public class MercariSellerSearchCondition {
    private Integer id;
    private String keyword;
    private String excludeKeyword;
    private String sellerId;
    private Integer rootCategory;
    private Integer duration;
    private String brand;
    private boolean enable;
    private String itemCondition;
    @TableField(exist = false)
    private List<String> conditionList = new ArrayList<>();
    @TableField(exist = false)
    private List<String> brandList = new ArrayList<>();
}
