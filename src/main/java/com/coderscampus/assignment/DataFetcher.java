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
		List<CompletableFuture<Integer>> futures = new ArrayList<>();
		
	for (int x=0; x < numberOfChunks; x++) {
				CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
					List<Integer> numbersList = assignment8.getNumbers();
					numbersList.stream()
							   .forEach(number -> {
								   counts.computeIfAbsent(number, k -> new AtomicInteger(0)).incrementAndGet();
							   });
					return numbersList.size();
				},
				exService);
				futures.add(future);
			}
			CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
			all.join();
			printTotalCounts(counts);
			
			exService.shutdown();

	}

	private static void printTotalCounts(Map<Integer, AtomicInteger> counts) {
		synchronized (counts) {
			
			StringBuilder result = new StringBuilder();

			for (int i = 0; i <= 14; i++) {

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
				.map(i -> {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					synchronized (counts) {
						counts.computeIfAbsent(i, k -> new AtomicInteger()).incrementAndGet();
				}
					return i;
				})
				.collect(Collectors.toList());
		
		return dataChunk;
		
		
	}
}
