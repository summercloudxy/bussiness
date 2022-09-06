package com.xy.bussiness.mercari.apibean;

public class ItemResponse {
	private String result;
	private ItemData data;
	private Meta meta;

	public String getResult(){
		return result;
	}

	public ItemData getData(){
		return data;
	}

	public Meta getMeta(){
		return meta;
	}
}
