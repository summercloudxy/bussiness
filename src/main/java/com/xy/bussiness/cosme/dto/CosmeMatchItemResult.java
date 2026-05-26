package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CosmeMatchItemResult {
    private Integer conditionId;
    private String keyword;
    private String description;
    private String brand;
    private String status;
    private String message;
    private boolean showAllProducts;
    private CosmeMatchCandidate bestMatch;
    private List<CosmeMatchCandidate> reviewCandidates = new ArrayList<>();
}
