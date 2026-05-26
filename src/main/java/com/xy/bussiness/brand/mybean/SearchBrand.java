package com.xy.bussiness.brand.mybean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("search_brand")
public class SearchBrand {
    @TableId
    private String nameEn;
    private String nameJa;
    private Integer mercariBrandId;
    private String cosmeKeyword;
}
