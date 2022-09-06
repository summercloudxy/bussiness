package com.xy.bussiness.mercari.apibean;

public class ShippingClass{
	private int shippingFee;
	private int pickupFee;
	private int fee;
	private int totalFee;
	private int id;
	private int iconId;
	private boolean isPickup;

	public int getShippingFee(){
		return shippingFee;
	}

	public int getPickupFee(){
		return pickupFee;
	}

	public int getFee(){
		return fee;
	}

	public int getTotalFee(){
		return totalFee;
	}

	public int getId(){
		return id;
	}

	public int getIconId(){
		return iconId;
	}

	public boolean isIsPickup(){
		return isPickup;
	}
}
