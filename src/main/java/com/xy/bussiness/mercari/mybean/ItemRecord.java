package com.xy.bussiness.mercari.mybean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data

@TableName("mercari_item_record")
public class ItemRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer searchConditionId;
    private String mercariItemId;
    private String mercariItemTitle;
    private boolean interest;
    private Integer originPrice;
    private Integer currentPrice;
    private Integer itemConditionId;
    private Date createDate;
    private Date updateDate;
    private String soldStatus;
    private String sellerId;
    private Date recordCreateDate;
    private String itemType;
    @TableField(exist = false)
    private String imageUrl;
    private boolean exclude = false;
}
