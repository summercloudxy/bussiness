package com.xy.bussiness.yahoo.mybean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("yahoo_item_record")
public class YahooItemRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer searchConditionId;
    private String title;
    private String auctionId;
    private Date endTime;
    private Integer buyNowPrice;
    private Integer originPrice;
    private Integer auctionPrice;
    private String imageUrl;
    private Boolean interest;
    private Date createDate;
    private Date updateDate;


}
