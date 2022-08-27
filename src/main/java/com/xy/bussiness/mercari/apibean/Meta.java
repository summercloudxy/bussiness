package com.xy.bussiness.mercari.apibean;

public class Meta{
	private String numFound;
	private String nextPageToken;
	private String previousPageToken;

	public String getNumFound(){
		return numFound;
	}

	public String getNextPageToken(){
		return nextPageToken;
	}

	public String getPreviousPageToken(){
		return previousPageToken;
	}
}
