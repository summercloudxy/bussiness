package com.xy.bussiness.mercari.mybean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("mercari_seller_item_record")
@Data
public class SellerItemRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String mercariItemId;
    private String mercariItemTitle;
    private boolean interest;
    private Integer originPrice;
    private Integer currentPrice;
    private Date createDate;
    private Date updateDate;
    private String soldStatus;
    private String sellerId;

}
