package com.xy.bussiness.mercari.apibean;

public class ItemCategory{
	private int parentCategoryId;
	private String name;
	private int displayOrder;
	private String parentCategoryName;
	private String rootCategoryName;
	private int rootCategoryId;
	private int id;
	private int brandGroupId;

	public int getParentCategoryId(){
		return parentCategoryId;
	}

	public String getName(){
		return name;
	}

	public int getDisplayOrder(){
		return displayOrder;
	}

	public String getParentCategoryName(){
		return parentCategoryName;
	}

	public String getRootCategoryName(){
		return rootCategoryName;
	}

	public int getRootCategoryId(){
		return rootCategoryId;
	}

	public int getId(){
		return id;
	}

	public int getBrandGroupId(){
		return brandGroupId;
	}
}
