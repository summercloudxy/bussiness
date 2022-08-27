package com.xy.bussiness.mercari.apibean;

import java.util.List;

public class ItemsResponse {
	private List<ComponentsItem> components;
	private Meta meta;
	private List<ItemsItem> items;

	public List<ComponentsItem> getComponents(){
		return components;
	}

	public Meta getMeta(){
		return meta;
	}

	public List<ItemsItem> getItems(){
		return items;
	}
}