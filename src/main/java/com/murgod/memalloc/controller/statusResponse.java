package com.murgod.memalloc.controller;

import org.springframework.stereotype.Component;

@Component
public class statusResponse {

	private int totalMemory;
	private int availableMemory;
	
	private int freeMemory;
	private int defaultHdrBlock;  // Always this value set to 1
	
	private int allocatedMemory;
	private int allocateHdrBlocks; // Each allocated block takes 4 byte header
	
	private int deallocatedMemory;
	private int deallocatedHdrBlocks; // Each Deallocated block takes 4 byte header

		
	private char Buffer[];
	
	
	
	public int getTotalMemory() {
		return totalMemory;
	}


	public void setTotalMemory(int totalMemory) {
		this.totalMemory = totalMemory;
	}


	public int getAvailableMemory() {
		return availableMemory;
	}


	public void setAvailableMemory(int availableMemory) {
		this.availableMemory = availableMemory;
	}


	public int getFreeMemory() {
		return freeMemory;
	}


	public void setFreeMemory(int freeMemory) {
		this.freeMemory = freeMemory;
	}


	public int getDefaultHdrBlock() {
		return defaultHdrBlock;
	}


	public void setDefaultHdrBlock(int defaultHdrBlock) {
		this.defaultHdrBlock = defaultHdrBlock;
	}


	public int getAllocatedMemory() {
		return allocatedMemory;
	}


	public void setAllocatedMemory(int allocatedMemory) {
		this.allocatedMemory = allocatedMemory;
	}


	public int getAllocateHdrBlocks() {
		return allocateHdrBlocks;
	}


	public void setAllocateHdrBlocks(int allocateHdrBlocks) {
		this.allocateHdrBlocks = allocateHdrBlocks;
	}


	public int getDeallocatedMemory() {
		return deallocatedMemory;
	}


	public void setDeallocatedMemory(int deallocatedMemory) {
		this.deallocatedMemory = deallocatedMemory;
	}


	public int getDeallocatedHdrBlocks() {
		return deallocatedHdrBlocks;
	}


	public void setDeallocatedHdrBlocks(int deallocatedHdrBlocks) {
		this.deallocatedHdrBlocks = deallocatedHdrBlocks;
	}


	public char[] getBuffer() {
		return Buffer;
	}


	public void setBuffer(byte[] buffer) {
		this.Buffer = new char[buffer.length];
		
		for(int i =0; i < buffer.length; i++) {
			this.Buffer[i] = (char) buffer[i];
		}
		//System.arraycopy(buffer, 0, Buffer, 0, buffer.length);
	}


		
	
}
