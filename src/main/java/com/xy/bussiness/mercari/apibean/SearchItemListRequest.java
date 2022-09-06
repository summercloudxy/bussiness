package com.xy.bussiness.mercari.apibean;

import lombok.Data;

import java.util.List;

@Data
public class SearchItemListRequest {
	private List<Object> defaultDatasets;
	private SearchCondition searchCondition;
	private String serviceFrom;
	private int pageSize;
	private String indexRouting;
	private String pageToken;
	private String searchSessionId;
	private String userId;
	private List<Object> thumbnailTypes;


}