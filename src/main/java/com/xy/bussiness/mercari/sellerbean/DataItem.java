package com.xy.bussiness.mercari.sellerbean;

import java.util.List;

public class DataItem{
	private Seller seller;
	private int created;
	private int root_category_id;
	private ItemBrand itemBrand;
	private ItemCategory itemCategory;
	private boolean liked;
	private long pagerId;
	private int itemPv;
	private int numComments;
	private ShippingFromArea shippingFromArea;
	private int price;
	private String name;
	private String id;
	private int numLikes;
	private List<String> thumbnails;
	private int updated;
	private String status;
	private TransactionEvidence transactionEvidence;
	private Buyer buyer;

	public Seller getSeller(){
		return seller;
	}

	public int getCreated(){
		return created;
	}


	public int getRoot_category_id() {
		return root_category_id;
	}

	public void setRoot_category_id(int root_category_id) {
		this.root_category_id = root_category_id;
	}

	public ItemBrand getItemBrand(){
		return itemBrand;
	}

	public ItemCategory getItemCategory(){
		return itemCategory;
	}

	public boolean isLiked(){
		return liked;
	}

	public long getPagerId(){
		return pagerId;
	}

	public int getItemPv(){
		return itemPv;
	}

	public int getNumComments(){
		return numComments;
	}

	public ShippingFromArea getShippingFromArea(){
		return shippingFromArea;
	}

	public int getPrice(){
		return price;
	}

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}

	public int getNumLikes(){
		return numLikes;
	}

	public List<String> getThumbnails(){
		return thumbnails;
	}

	public int getUpdated(){
		return updated;
	}

	public String getStatus(){
		return status;
	}

	public TransactionEvidence getTransactionEvidence(){
		return transactionEvidence;
	}

	public Buyer getBuyer(){
		return buyer;
	}
}