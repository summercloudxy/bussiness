package com.xy.bussiness.mercari.apibean;

public class Seller{
	private int numSellItems;
	private int created;
	private boolean quickShipper;
	private int score;
	private int starRatingScore;
	private boolean isOfficial;
	private String registerSmsConfirmation;
	private int numRatings;
	private Ratings ratings;
	private String name;
	private String registerSmsConfirmationAt;
	private int id;
	private String photoUrl;
	private String photoThumbnailUrl;

	public int getNumSellItems(){
		return numSellItems;
	}

	public int getCreated(){
		return created;
	}

	public boolean isQuickShipper(){
		return quickShipper;
	}

	public int getScore(){
		return score;
	}

	public int getStarRatingScore(){
		return starRatingScore;
	}

	public boolean isIsOfficial(){
		return isOfficial;
	}

	public String getRegisterSmsConfirmation(){
		return registerSmsConfirmation;
	}

	public int getNumRatings(){
		return numRatings;
	}

	public Ratings getRatings(){
		return ratings;
	}

	public String getName(){
		return name;
	}

	public String getRegisterSmsConfirmationAt(){
		return registerSmsConfirmationAt;
	}

	public int getId(){
		return id;
	}

	public String getPhotoUrl(){
		return photoUrl;
	}

	public String getPhotoThumbnailUrl(){
		return photoThumbnailUrl;
	}
}
