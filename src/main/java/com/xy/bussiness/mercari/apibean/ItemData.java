package com.xy.bussiness.mercari.apibean;

import java.util.List;

public class ItemData {
	private Seller seller;
	private ApplicationAttributes applicationAttributes;
	private List<String> photoPaths;
	private boolean isStockItem;
	private ShippingDuration shippingDuration;
	private boolean isAnonymousShipping;
	private boolean isCancelable;
	private String description;
	private List<String> photos;
	private boolean isOrganizationalUser;
	private ItemCategory itemCategory;
	private List<ColorsItem> colors;
	private boolean liked;
	private long pagerId;
	private int numComments;
	private ShippingPayer shippingPayer;
	private int price;
	private ShippingMethod shippingMethod;
	private boolean isDynamicShippingFee;
	private String isShopItem;
	private ItemCondition itemCondition;
	private String checksum;
	private String id;
	private int numLikes;
	private boolean isOfferableV2;
	private List<String> hashTags;
	private String organizationalUserStatus;
	private boolean hasLikeList;
	private List<Object> comments;
	private int created;
	private boolean isOfferable;
	private ItemBrand itemBrand;
	private boolean isWebVisible;
	private boolean shippedByWorker;
	private ShippingFromArea shippingFromArea;
	private List<Object> additionalServices;
	private String name;
	private ShippingClass shippingClass;
	private List<String> thumbnails;
	private int updated;
	private boolean hasAdditionalService;
	private String status;

	public Seller getSeller(){
		return seller;
	}

	public ApplicationAttributes getApplicationAttributes(){
		return applicationAttributes;
	}

	public List<String> getPhotoPaths(){
		return photoPaths;
	}

	public boolean isIsStockItem(){
		return isStockItem;
	}

	public ShippingDuration getShippingDuration(){
		return shippingDuration;
	}

	public boolean isIsAnonymousShipping(){
		return isAnonymousShipping;
	}

	public boolean isIsCancelable(){
		return isCancelable;
	}

	public String getDescription(){
		return description;
	}

	public List<String> getPhotos(){
		return photos;
	}

	public boolean isIsOrganizationalUser(){
		return isOrganizationalUser;
	}

	public ItemCategory getItemCategory(){
		return itemCategory;
	}

	public List<ColorsItem> getColors(){
		return colors;
	}

	public boolean isLiked(){
		return liked;
	}

	public long getPagerId(){
		return pagerId;
	}

	public int getNumComments(){
		return numComments;
	}

	public ShippingPayer getShippingPayer(){
		return shippingPayer;
	}

	public int getPrice(){
		return price;
	}

	public ShippingMethod getShippingMethod(){
		return shippingMethod;
	}

	public boolean isIsDynamicShippingFee(){
		return isDynamicShippingFee;
	}

	public String getIsShopItem(){
		return isShopItem;
	}

	public ItemCondition getItemCondition(){
		return itemCondition;
	}

	public String getChecksum(){
		return checksum;
	}

	public String getId(){
		return id;
	}

	public int getNumLikes(){
		return numLikes;
	}

	public boolean isIsOfferableV2(){
		return isOfferableV2;
	}

	public List<String> getHashTags(){
		return hashTags;
	}

	public String getOrganizationalUserStatus(){
		return organizationalUserStatus;
	}

	public boolean isHasLikeList(){
		return hasLikeList;
	}

	public List<Object> getComments(){
		return comments;
	}

	public int getCreated(){
		return created;
	}

	public boolean isIsOfferable(){
		return isOfferable;
	}

	public ItemBrand getItemBrand(){
		return itemBrand;
	}

	public boolean isIsWebVisible(){
		return isWebVisible;
	}

	public boolean isShippedByWorker(){
		return shippedByWorker;
	}

	public ShippingFromArea getShippingFromArea(){
		return shippingFromArea;
	}

	public List<Object> getAdditionalServices(){
		return additionalServices;
	}

	public String getName(){
		return name;
	}

	public ShippingClass getShippingClass(){
		return shippingClass;
	}

	public List<String> getThumbnails(){
		return thumbnails;
	}

	public int getUpdated(){
		return updated;
	}

	public boolean isHasAdditionalService(){
		return hasAdditionalService;
	}

	public String getStatus(){
		return status;
	}
}