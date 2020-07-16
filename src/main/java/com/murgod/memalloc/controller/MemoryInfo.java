package com.murgod.memalloc.controller;

import org.springframework.stereotype.Component;

@Component
public class MemoryInfo {

	private int blockzise;
	private int allocatedSize;
	private int tag;

	public int getBlockzise() {
		return blockzise;
	}
	public void setBlockzise(int blockzise) {
		this.blockzise = blockzise;
	}
	public int getAllocatedSize() {
		return allocatedSize;
	}
	
	
	public void setAllocatedSize(int allocatedSize) {
		this.allocatedSize += allocatedSize;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	

}
