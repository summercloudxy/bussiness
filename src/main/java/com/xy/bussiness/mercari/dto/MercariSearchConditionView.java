package com.xy.bussiness.mercari.dto;

import com.xy.bussiness.mercari.mybean.MercariSearchCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MercariSearchConditionView extends MercariSearchCondition {
    private List<MercariItemThumb> latestItems = new ArrayList<>();
}
