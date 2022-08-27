package com.xy.bussiness.rakuten.mybean;

import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.Data;

@Data
public class SearchRequest {
    RakutenSearchCondition searchCondition;
    Integer pageNum;

    public SearchRequest(RakutenSearchCondition searchCondition, Integer pageNum) {
        this.searchCondition = searchCondition;
        this.pageNum = pageNum;
    }
}
