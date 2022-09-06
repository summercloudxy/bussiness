package com.xy.bussiness.mercari.apibean;

public class ShippingDuration{
	private String name;
	private int minDays;
	private int maxDays;
	private int id;

	public String getName(){
		return name;
	}

	public int getMinDays(){
		return minDays;
	}

	public int getMaxDays(){
		return maxDays;
	}

	public int getId(){
		return id;
	}
}
