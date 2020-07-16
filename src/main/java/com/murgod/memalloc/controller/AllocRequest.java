package com.murgod.memalloc.controller;

public class AllocRequest {
	public String getmbytes() {
		return mbytes;
	}

	public void setmbytes(String mbytes) {
		this.mbytes = mbytes;
	}

	private String mbytes;

}


class DeallocRequest {
	private String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}