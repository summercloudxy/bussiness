package com.xy.bussiness.cosme.dto;

import lombok.Data;

@Data
public class CosmeMatchCandidate {
    private String productId;
    private String productName;
    private String productUrl;
    private String imageUrl;
    private String brand;
    private double similarity;
    private String matchedReference;
    private String referenceType;
}
