package com.xy.bussiness.rakuten.mybean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("rakuten_item_record")
@Data
public class RakutenItemRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer searchConditionId;
    private String itemId;
    private String title;
    private boolean interest;
    private Integer originPrice;
    private Integer currentPrice;
    private Date createDate;
    private Date updateDate;
    private String imageUrl;
    private String itemUrl;

}
