package com.murgod.memalloc.controller;

public class NewRequest {
	private String pageSize;
	private String noOfPages;
	
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getNoOfPages() {
		return noOfPages;
	}
	public void setNoOfPages(String noOfPages) {
		this.noOfPages = noOfPages;
	}
}
