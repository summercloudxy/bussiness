package com.xy.bussiness.rakuten.dto;

import com.xy.bussiness.rakuten.mybean.RakutenSearchCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RakutenSearchConditionView extends RakutenSearchCondition {
    private List<RakutenItemThumb> latestItems = new ArrayList<>();
}
