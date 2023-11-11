package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataFetcher {

	public static void main(String[] args) {
		int numberOfChunks = 1000;
		
		Assignment8 assignment8 = new Assignment8();
		Map<Integer, AtomicInteger> counts = new ConcurrentHashMap<>();
		ExecutorService exService = Executors.newCachedThreadPool();
		List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();
		
		for (int i=0; i<numberOfChunks; i++) {
			final int chunk = i;
			CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> 
												fetchDataChunk(chunk, assignment8, counts, exService), exService).exceptionally(ex -> {
													System.err.println("Exception occurred: " + ex.getMessage());
													return new ArrayList<>();
												});
			futures.add(future);
		}

		CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		
		all.thenRun(() -> {
			printTotalCounts(counts);
		});
		
		all.join();

		exService.shutdown();

	}

	private static void printTotalCounts(Map<Integer, AtomicInteger> counts) {
		synchronized (counts) {
			
			StringBuilder result = new StringBuilder();

			for (int i = 1; i <= 14; i++) {

				int count = counts.getOrDefault(i, new AtomicInteger(0)).get();
				result.append(i).append("=").append(count);
				
				if (i < 14) {
					result.append(", ");
				}
			}
			
			System.out.println(result);
		}
	}
	private static List<Integer> fetchDataChunk(int chunk, Assignment8 assignment8, Map<Integer, AtomicInteger> counts, ExecutorService exService) {
		int chunkSize = 1000;
		int start = chunk * chunkSize;
		int end = start + chunkSize;
				
		List<Integer> dataChunk = IntStream.range(start, end)
				.boxed()
				.map(n -> assignment8.getNumbers(n, n+1))
				.flatMap(List::stream)
				.limit(15)
				.map(i -> {
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					counts.computeIfAbsent(i, k -> new AtomicInteger()).incrementAndGet();
					return i;
				})
				.collect(Collectors.toList());

		return dataChunk;
		
		
	}
}
