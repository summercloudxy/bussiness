package com.xy.bussiness.mercari.apibean;

import lombok.Data;

import java.util.List;

@Data
public class SearchCondition{
	private List<Object> sizeId;
	private List<Object> itemTypes;
	private int priceMax;
	private List<Object> shippingPayerId;
	private String excludeKeyword;
	private List<Object> colorId;
	private List<Object> shippingMethod;
	private String sort;
	private List<Object> skuIds;
	private int priceMin;
	private List<Object> sellerId;
	private boolean hasCoupon;
	private List<Object> shippingFromArea;
	private List<Object> brandId;
	private List<Object> attributes;
	private String keyword;
	private List<Object> itemConditionId;
	private List<Integer> categoryId;
	private String order;
	private List<String> status;


}