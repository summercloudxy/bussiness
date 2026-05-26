package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CosmeSearchResult {
    private String keyword;
    private String categoryId;
    private int page;
    private int total;
    private int pageSize;
    private int totalPages;
    private String cosmeUrl;
    private List<CosmeProduct> products = new ArrayList<>();
    private List<CosmeCategoryFilter> categories = new ArrayList<>();
}
