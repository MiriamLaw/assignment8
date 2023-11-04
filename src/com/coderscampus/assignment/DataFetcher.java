package com.coderscampus.assignment;

import java.util.concurrent.CompletableFuture;

public class DataFetcher {

	public static void main(String[] args) {
		
		CompletableFuture<List<Integer>> futures = new CompletableFuture[1000];
		
		for (int i=0; i<1000; i++) {
			final int chunk = i;
			
			CompleteableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> 
												fetchDataChunk(chunk));
			futures[i] = future;
		}
		//combine into one completablefuture when done
		CompletableFuture<Void> all = CompletableFuture.allOf(futures);
		
		all.thenRun(() -> {
			for (int i=0; i<1000; i++) {
				List<Integer> dataChunk = futures[i].join();
				System.out.println(dataChunk);
			}
		});
		//ensure program doesn't terminate immediately
		all.join();
		
	}
	private static List<Integer> fetchDataChunk(int chunk) {
		Assignment8 assignment8 = new Assignment8();
		
		int start = chunk * 1000;
		int end = (chunk + 1) * 1000;
		
		List<Integer> dataChunk = assignment8.getNumbers(start, end);
		
		return dataChunk;
		
		
	}
}
