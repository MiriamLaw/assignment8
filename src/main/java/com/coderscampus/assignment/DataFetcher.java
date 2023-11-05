package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DataFetcher {

	public static void main(String[] args) {
		
		List<CompletableFuture<Integer>> futures = new ArrayList<>();
		
		for (int i=0; i<1000; i++) {
			final int chunk = i;
			
			CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 
												fetchDataChunk(chunk).size());
			futures.add(future);
		}
		//combine into one completablefuture when done
		CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		
//		all.thenRun(() -> {
//			for (int i=0; i<1000; i++) {
//				List<Integer> dataChunk = futures[i].join();
//				System.out.println(dataChunk);
//			}
//		});
		//ensure program doesn't terminate immediately
		all.join();
		
		List<Integer> results = futures.stream()
										.map(CompletableFuture::join)
										.collect(Collectors.toList());
		
		System.out.println(results);
		
	}
	private static List<Integer> fetchDataChunk(int chunk) {
		Assignment8 assignment8 = new Assignment8();
		int chunkSize = 1000;
		int start = chunk * chunkSize;
		int end = start + chunkSize;
		
		List<Integer> dataChunk = new ArrayList<>();
		while (start < end) {
			List<Integer> numbersChunk = assignment8.getNumbers();
			if (start >= numbersChunk.size()) {
				break;
			}
			start += chunkSize;
		}
		return dataChunk;
		
		
	}
}
