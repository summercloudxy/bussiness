package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CosmeProduct {
    private String productId;
    private String name;
    private String brand;
    private String category;
    private String price;
    private String imageUrl;
    private String productUrl;
    private boolean keywordAdded;
    private int keywordCount;
    private boolean mercariAdded;
    private boolean rakutenAdded;
    private boolean yahooAdded;
    private int mercariKeywordCount;
    private int rakutenKeywordCount;
    private int yahooKeywordCount;
    private List<String> addedKeywords = new ArrayList<>();
    private List<String> mercariKeywords = new ArrayList<>();
    private List<String> rakutenKeywords = new ArrayList<>();
    private List<String> yahooKeywords = new ArrayList<>();
}
