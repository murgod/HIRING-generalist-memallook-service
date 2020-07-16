package com.murgod.memalloc.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemController {
	
	@Autowired
	private MemoryInfo info;
	
	@Autowired 
	private statusResponse status;
	
	byte Buffer[] = null;
	int BufferSize = 0;
	int alloc_req_num = 0;
	
	private final int BYTE_BOUNDARY = 4;
	private final int HEADER_SIZE = 4;
	HashMap<String, Integer> Tags = new HashMap<>();
	
	
	
	public static  long packUInt32(Byte b0, Byte b1, Byte b2, Byte b3){
	    long value = 
	        ((b0 & 0xFF) <<  0) |
	        ((b1 & 0xFF) <<  8) |
	        ((b2 & 0xFF) << 16) |
	        ((b3 & 0xFF) << 24);
	    return value;
	}
 
	@PostMapping(path = "/memalloc/new", consumes = { MediaType.APPLICATION_JSON_VALUE})
	public String newHandler(@RequestBody NewRequest request) {

		System.out.println("Page_Size: "+request.getPageSize());
		System.out.println("No_of_Pages: "+request.getNoOfPages());
		
		int P = Integer.parseInt(request.getPageSize());
		int N = Integer.parseInt(request.getNoOfPages());
		
		this.BufferSize = P * N;
		
		if(this.Buffer == null)
			this.Buffer = new byte[BufferSize];
		else
			return "Error trying to allocate Buffer which is already allocated";
		
		Arrays.fill(Buffer, (byte)'f');
		
		Buffer[0] = 'F';  // FREE BUFFER INDICATOR 
		Buffer[1] = 'R';  // RESERVER FOR FUTURE USE
		int size = (this.BufferSize +  BYTE_BOUNDARY - 1)/ BYTE_BOUNDARY - (HEADER_SIZE/BYTE_BOUNDARY);
		
		Buffer[2]  = (byte) ((size >> 8) & 0xff);  //SIZE MSB
		Buffer[3]  = (byte) (size & 0xff); // SIZE LSB
		
		System.out.println("NEW BUFFER ALLOCATED");
		System.out.println("size :"+size);
		System.out.println(Arrays.toString(Buffer));
		
		return "Buffer of Size " + Integer.toString(BufferSize) + " Bytes created";
		
	}
	

/*
 * 
 * 
 * 
 * */
	
	public String allocate(int reqBytes) {
		int size = 0;
		int Avl_buf_size = 0;
		
		for(int i = 0; i < this.BufferSize; i++) {
			if(Buffer[i] == 'D') {
				size = (reqBytes +  BYTE_BOUNDARY - 1)/ BYTE_BOUNDARY;
				
				//System.out.println("Buffer[i+2]  :"+Buffer[i+2] );
				//System.out.println("Buffer[i+3]  :"+Buffer[i+3]);
				Avl_buf_size = ((Buffer[i+2] & 0xFF) << 8) | ((Buffer[i+3] & 0xFF) << 0);
				
				//System.out.println("size :"+size);
				//System.out.println("Avl_buf_size :"+Avl_buf_size);
				
				if(size > Avl_buf_size) { // Not enough space left for allocation
					continue;
				}
				
				Buffer[i] = 'A'; 
				Buffer[i+1] = 'R';
				Buffer[i+2] = (byte) ((size >> 8) & 0xFF); // MSB
				Buffer[i+3] = (byte) (size & 0xFF); // LSB
						
				int start = i + 4;
				int j = 0;
				
				for(j = start; j < (start + size * 4); j++) {
					Buffer[j] = 'x';
				} 
							
				/*
				 * Adjust remaining space in the Deallocated block
				 * */
				int remSize = Avl_buf_size - size - 1;
				
				if(remSize > 1) {

					Buffer[i] = 'D';
					Buffer[i+1] = 'R';
					Buffer[i+2] = (byte) ((remSize >> 8) & 0xFF);
					Buffer[i+3] = (byte) (remSize & 0xFF);

					int st = i + 4;
					int end = st + size*BYTE_BOUNDARY;

					Arrays.fill(Buffer, st, end, (byte)'f');
				}
				
				System.out.println("MEMORY ALLOCATED");
				System.out.println("SIZE:" +size);
				System.out.println(Arrays.toString(Buffer));
				
				String tag = "BLOCK-"+Integer.toString(alloc_req_num);
				Tags.put(tag, i);
				
				return tag;
				
			}
			else if(Buffer[i] == 'F') {
				// Ceiling division. Ex: If reqBytes = 10 Bytes then size = 3.
				size = (reqBytes +  BYTE_BOUNDARY - 1)/ BYTE_BOUNDARY;
				
				//System.out.println("Buffer[i+2]  :"+Buffer[i+2]);
				//System.out.println("Buffer[i+3]  :"+Buffer[i+3]);
				Avl_buf_size = ((Buffer[i+2] & 0xFF) << 8) | ((Buffer[i+3] & 0xFF) << 0);
				
				System.out.println("size :"+size);
				System.out.println("Avl_buf_size :"+Avl_buf_size);
				
				if(size > Avl_buf_size) { // Not enough space left for allocation
					return "Not enough space in the buffer";
				}
				
				
				Buffer[i] = 'A'; 
				Buffer[i+1] = 'R';
				Buffer[i+2] = (byte) ((size >> 8) & 0xFF); // MSB
				Buffer[i+3] = (byte) (size & 0xFF); // LSB
							
				int start = i + 4;
				int j = 0;
				
				for(j = start; j < (start + size * 4); j++) {
					Buffer[j] = 'x';
				} 
				
			    Buffer[j+0] = 'F';
			    Buffer[j+1] = 'R';
			    int remSize = Avl_buf_size - size - 1;
				Buffer[j+2] = (byte) ((remSize >> 8) & 0xFF); // MSB
				Buffer[j+3] = (byte) (remSize & 0xFF); // LSB
			    
						
				System.out.println("MEMORY ALLOCATED");
				System.out.println("SIZE:" +size);
				System.out.println(Arrays.toString(Buffer));
								
				String tag = "BLOCK-"+Integer.toString(alloc_req_num);
				Tags.put(tag, i);
				
				return tag;
			}
		}
		
		return " ";
		
		
	}
	
	@PostMapping(path = "/memalloc/alloc", consumes = { MediaType.APPLICATION_JSON_VALUE})
	public String allocHandler(@RequestBody AllocRequest request) {

		System.out.println("ALLOC REQUEST RECEIVED WITH SIZE : "+request.getmbytes());
		int reqBytes = Integer.parseInt(request.getmbytes());
		
		this.alloc_req_num++;
		
		
		
		if(Buffer == null || BufferSize == 0)
			//return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
			return "Error! No memory buffer found. Run new API to allocate buffer";
		
		else if(reqBytes > BufferSize)
			return "Error! Requested alloc size is greater than Total Buffer Size";
	
//		else if(reqBytes + 4 > maxFreeBlock())
//			return "Error! Not enough memroy to allocate";
		
		return allocate(reqBytes);
		
	}
	
	@PostMapping(path = "/memalloc/dealloc", consumes = { MediaType.APPLICATION_JSON_VALUE})
	public String deallocHandler(@RequestBody DeallocRequest request) {

		System.out.println("DE-ALLOC REQUEST RECEIVED WITH BLOCK :"+request.getTag());
		String tag = request.getTag();
		
		boolean nextBlockFree = false;
		
		if(Tags.containsKey(tag)) {
			int index = Tags.get(tag);
			
			if(Buffer[index] == 'A') {
				// Case1: Previous block is allocated and Next Block is allocated.
				// Case2: Previous block is allocated and no Next Block (Current block is last block).
				// Case3: Previous block is allocated and Next Block is released 
				// Case4: Previous Block is Released and Next Block is allocated.
				// Case5: Previous Block is Released and no Next Block.
				// Case5: Previous Block is Released and Next Block is released.
				// Case6: No previous block and Next block is allocated.
				// Case7: No previous block and no Next block.
				
				int size = ((Buffer[index+2] & 0xFF) << 8) | ((Buffer[index+3] & 0xFF) << 0);
				
				System.out.println("Dealloc Size : "+Integer.toString(size));
				
				
				/************************************
				 * Adjusting memory blocks forwards
				 * 
				 * */
				
				int indexNextBlock = index + size * BYTE_BOUNDARY + HEADER_SIZE;
				
				if(Buffer[indexNextBlock] == 'F') {
					
					Buffer[indexNextBlock] = 'f';
					Buffer[indexNextBlock + 1] = 'f';
					int NextBlocksize = ((Buffer[indexNextBlock+2] & 0xFF) << 8) | ((Buffer[indexNextBlock+3] & 0xFF) << 0);
					
					Buffer[indexNextBlock + 2] = 'f';
					Buffer[indexNextBlock + 3] = 'f';
					System.out.println("size :"+size);
					System.out.println("NextBlocksize :"+NextBlocksize);
					size = size + NextBlocksize + 1;
					nextBlockFree = true ;
					
					
				}else if(Buffer[indexNextBlock] == 'D') {
					Buffer[indexNextBlock] = 'f';
					Buffer[indexNextBlock + 1] = 'f';
					int NextBlocksize = ((Buffer[indexNextBlock+2] & 0xFF) << 8) | ((Buffer[indexNextBlock+3] & 0xFF) << 0);
					
					Buffer[indexNextBlock + 2] = 'f';
					Buffer[indexNextBlock + 3] = 'f';
					
					size = size + NextBlocksize+1;
					
					int indexNextBlock1 = indexNextBlock + NextBlocksize * BYTE_BOUNDARY + HEADER_SIZE;
					
					if(Buffer[indexNextBlock1] == 'F') {
						
						Buffer[indexNextBlock1] = 'f';
						Buffer[indexNextBlock1 + 1] = 'f';
						int NextBlocksize1 = ((Buffer[indexNextBlock1+2] & 0xFF) << 8) | ((Buffer[indexNextBlock1+3] & 0xFF) << 0);
						
						Buffer[indexNextBlock1 + 2] = 'f';
						Buffer[indexNextBlock1 + 3] = 'f';
						
						size = size + NextBlocksize1+1;
					}
					nextBlockFree = false;
					
				}
				
				if(nextBlockFree)
					Buffer[index] = 'F';
				else
					Buffer[index] = 'D';
				
				Buffer[index+1] = 'R';
				Buffer[index+2] = (byte) ((size >> 8) & 0xFF);
				Buffer[index+3] = (byte) (size & 0xFF);
				
				int start = index + 4;
				int end = start + size*BYTE_BOUNDARY;
				
				Arrays.fill(Buffer, start, end, (byte)'f');
				
				/************************************
				 * Adjusting memory blocks backwards
				 * 
				 ********************/
				
				int indexPrevBlock = 0;
				
				for(indexPrevBlock = index-1; indexPrevBlock >= 0; indexPrevBlock--) {
					byte temp = Buffer[indexPrevBlock]; 
					
					if(temp == 'D') {
						System.out.println("Execution here !!!!!!!");
						
						Buffer[indexPrevBlock] = 'D';
						Buffer[indexPrevBlock+1] = 'R';
						
						int prevBlocksize = ((Buffer[indexPrevBlock + 2] & 0xFF) << 8) | ((Buffer[indexPrevBlock + 3] & 0xFF) << 0);
						int newSize = size  + prevBlocksize+1;
						
						Buffer[indexPrevBlock+2] = (byte) ((newSize >> 8) & 0xFF);
						Buffer[indexPrevBlock+3] = (byte) (newSize & 0xFF);
						
						int start1 = prevBlocksize + 4;
						int end1 = start1 + newSize*BYTE_BOUNDARY;
						
						Arrays.fill(Buffer, start1, end1, (byte)'f');
						
						Buffer[index] = 'f';
						Buffer[index+1] = 'f';
						Buffer[index+2] = 'f';
						Buffer[index+3] = 'f';
					    break;	
					}
				
					
				}
				
				Tags.remove(tag);
				System.out.println("MEMORY DEALLOCATED");
				System.out.println(Arrays.toString(Buffer));
				
				return "SUCCESS: Memory block deallocated successfully";
			}
			else {
				return "ERROR: Something went wrong please check";
			}
			
			
			
		}
		else {
			return "ERROR: Dealloc failed. Memory block with giveb TAG does not exist";	
		}
	}
	
	@GetMapping(path = "/memalloc/show")
	public statusResponse showHandler() {
		if(this.Buffer == null)
			return status;
		
		status.setTotalMemory(this.BufferSize);
		
		
		int totalMemory = 0;
		int availableMemory = 0;
		
		int freeMemory = 0;
		int defaultHdrBlock = 0;  // Always this value set to 1
		
		int allocatedMemory = 0;
		int allocateHdrBlocks = 0; // Each allocated block takes 4 byte header
		
		int deallocatedMemory = 0;
		int deallocatedHdrBlocks = 0; // Each Deallocated block takes 4 byte header


		for(int i = 0; i < this.BufferSize; i++) {
			if(Buffer[i] == 'D') {
				deallocatedHdrBlocks++;
				int size = ((Buffer[i+2] & 0xFF) << 8) | ((Buffer[i+3] & 0xFF) << 0);
				deallocatedMemory += (size * BYTE_BOUNDARY);
			}
			if(Buffer[i] == 'F') {
				defaultHdrBlock++;
				int size = ((Buffer[i+2] & 0xFF) << 8) | ((Buffer[i+3] & 0xFF) << 0);
				freeMemory += (size * BYTE_BOUNDARY);
			}
			if(Buffer[i] == 'A') {
				allocateHdrBlocks++;
				int size = ((Buffer[i+2] & 0xFF) << 8) | ((Buffer[i+3] & 0xFF) << 0);
				allocatedMemory += (size * BYTE_BOUNDARY);
			}
		}
		
		availableMemory = (freeMemory + deallocatedMemory);
		
		defaultHdrBlock = defaultHdrBlock * BYTE_BOUNDARY;
		deallocatedHdrBlocks = deallocatedHdrBlocks * BYTE_BOUNDARY;
		allocateHdrBlocks = allocateHdrBlocks * BYTE_BOUNDARY;
		
		
		totalMemory = this.BufferSize;
		status.setTotalMemory(totalMemory);
		status.setAvailableMemory(availableMemory);
		
		status.setFreeMemory(freeMemory);
		status.setDefaultHdrBlock(defaultHdrBlock);
		
		status.setAllocatedMemory(allocatedMemory);
		status.setAllocateHdrBlocks(allocateHdrBlocks);
		
		status.setDeallocatedMemory(deallocatedMemory);
		status.setDeallocatedHdrBlocks(deallocatedHdrBlocks);
		
		status.setBuffer(Buffer);
		
		return status;
		
	}
	@PostMapping(path = "/memalloc/reset")
	public String resetHandler() {
		
		if(this.Buffer != null)
			this.Buffer = null;
		else
			return "ERROR: Reset failed. No memory to reset";
		
		return "RESET successful";
		
		
	}
	
	/*
	 * 
	 * DEFRAG is done along with DEALLOC, this below implementation is just printing the status*/
	@PostMapping(path = "/memalloc/defrag")
	public String defragHandler() {
		
		showHandler();
		
		return "DEFRAG successful";
		
		
	}
}
