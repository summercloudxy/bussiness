package com.xy.bussiness.cosme.dto;

import lombok.Data;

@Data
public class ConfirmCosmeMatchRequest {
    private Integer conditionId;
    private String cosmeProductId;
    private String cosmeProductUrl;
    private String cosmeProductName;
    private String cosmeProductImage;
}
