package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataFetcher {

	public static void main(String[] args) {
		int numberOfChunks = 1000;
		
		Assignment8 assignment8 = new Assignment8();
		ExecutorService exService = Executors.newCachedThreadPool();
		
		List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();
		
		for (int i=0; i<numberOfChunks; i++) {
			final int chunk = i;
			
			CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> 
												fetchDataChunk(chunk, assignment8, exService), exService).exceptionally(ex -> {
													System.err.println("Exception occurred: " + ex.getMessage());
													return new ArrayList<>();
												});
			futures.add(future);
		}
		//combine into one completablefuture when done
		CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		
		
		//ensure program doesn't terminate immediately
		all.join();
		
		List<Integer> results = futures.stream()
										.map(CompletableFuture::join)
										.flatMap(List::stream)
										.collect(Collectors.toList());
		
		calculateFrequency(results);
		
		exService.shutdown();
		
	}
	private static void calculateFrequency(List<Integer> results) {
		System.out.println("calculateFrequency method called with results: " + results);
		Map<Integer, Integer> frequencyMap = new HashMap<>();
		
		for (Integer number : results) {
			frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
		}
		
		for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
	}
	private static List<Integer> fetchDataChunk(int chunk, Assignment8 assignment8, ExecutorService exService) {
		int chunkSize = 1000;
		int start = chunk * chunkSize;
		int end = start + chunkSize;
		
		System.out.println("Processing chunk: " + chunk + " from " + start + " to " + end);
		
		List<Integer> dataChunk = IntStream.range(start, end)
				.boxed()
				.map(n -> assignment8.getNumbers(n, n+1))
				.flatMap(List::stream)
				.parallel()
				.limit(10)
				.map(i -> {
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					return i;
				})
				.collect(Collectors.toList());
		
		return dataChunk;
		
		
	}
	

}
