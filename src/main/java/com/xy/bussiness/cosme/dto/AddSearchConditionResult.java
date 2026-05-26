package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddSearchConditionResult {
    private boolean success;
    private String message;
    private Integer mercariId;
    private Integer rakutenId;
    private Integer yahooId;
    private List<String> warnings = new ArrayList<>();
}
