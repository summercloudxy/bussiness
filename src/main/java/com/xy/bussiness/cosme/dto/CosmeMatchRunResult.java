package com.xy.bussiness.cosme.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CosmeMatchRunResult {
    private int processed;
    private int autoApplied;
    private int pending;
    private int notFound;
    private int skipped;
    private List<CosmeMatchItemResult> items = new ArrayList<>();
}
