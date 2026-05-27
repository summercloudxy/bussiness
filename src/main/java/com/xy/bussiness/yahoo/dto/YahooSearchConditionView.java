package com.xy.bussiness.yahoo.dto;

import com.xy.bussiness.yahoo.mybean.YahooSearchCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class YahooSearchConditionView extends YahooSearchCondition {
    private List<YahooItemThumb> latestItems = new ArrayList<>();
}
