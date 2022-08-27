package com.xy.bussiness.yahoo.mybean;

import lombok.Data;

@Data
public class SearchRequest {
    YahooSearchCondition searchCondition;
    Integer pageNum;

    public SearchRequest(YahooSearchCondition searchCondition, Integer pageNum) {
        this.searchCondition = searchCondition;
        this.pageNum = pageNum;
    }
}
