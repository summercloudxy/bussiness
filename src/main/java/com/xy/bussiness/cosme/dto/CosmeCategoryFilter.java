package com.xy.bussiness.cosme.dto;

import lombok.Data;

@Data
public class CosmeCategoryFilter {
    private String id;
    private String nameJa;
    private String nameZh;
    private String label;
    private int count;
    private int level = 1;
    private boolean selectable;
    private boolean selected;
}
