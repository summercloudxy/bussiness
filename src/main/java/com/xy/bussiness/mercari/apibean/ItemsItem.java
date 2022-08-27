package com.xy.bussiness.mercari.apibean;

import java.util.List;

public class ItemsItem{
	private String itemType;
	private String shippingPayerId;
	private String sellerId;
	private String price;
	private String created;
	private String name;
	private String id;
	private String buyerId;
	private List<String> thumbnails;
	private String itemConditionId;
	private String updated;
	private String status;

	public String getItemType(){
		return itemType;
	}

	public String getShippingPayerId(){
		return shippingPayerId;
	}

	public String getSellerId(){
		return sellerId;
	}

	public String getPrice(){
		return price;
	}

	public String getCreated(){
		return created;
	}

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}

	public String getBuyerId(){
		return buyerId;
	}

	public List<String> getThumbnails(){
		return thumbnails;
	}

	public String getItemConditionId(){
		return itemConditionId;
	}

	public String getUpdated(){
		return updated;
	}

	public String getStatus(){
		return status;
	}
}