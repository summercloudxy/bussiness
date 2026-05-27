package com.xy.bussiness.mercari.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageResult<T> {
    private long total;
    private long page;
    private long pageSize;
    private List<T> records = new ArrayList<>();
}
