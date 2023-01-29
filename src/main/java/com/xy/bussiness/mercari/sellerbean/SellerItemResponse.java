package com.xy.bussiness.mercari.sellerbean;

import java.util.List;

public class SellerItemResponse{
	private String result;
	private List<DataItem> data;
	private Meta meta;

	public String getResult(){
		return result;
	}

	public List<DataItem> getData(){
		return data;
	}

	public Meta getMeta(){
		return meta;
	}
}