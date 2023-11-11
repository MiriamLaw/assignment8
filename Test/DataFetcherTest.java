import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.junit.jupiter.api.Test;

import com.coderscampus.assignment.Assignment8;

class DataFetcherTest {

	@Test
	public void testConcurrentDataProcessing() {
		Assignment8 assignment8 = new Assignment8();
		Map<Integer, AtomicInteger> counts = new ConcurrentHashMap<>();
		ExecutorService exService = Executors.newCachedThreadPool();
		List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();

		int totalIntegers = 1000000;
		int chunkSize = 1000;
		int numberOfChunks = totalIntegers / chunkSize;

		for (int i = 0; i < numberOfChunks; i++) {
			final int chunk = i;
			CompletableFuture<List<Integer>> future = CompletableFuture
					.supplyAsync(() -> fetchDataChunk(chunk, assignment8, counts, exService), exService)
					.exceptionally(ex -> {
						System.err.println("Exception occurred: " + ex.getMessage());
						return new ArrayList<>();
					});
			futures.add(future);
		}

		CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		all.thenRun(() -> {
			assertEquals(numberOfChunks * 15, counts.size());
			assertEquals(numberOfChunks * 10, futures.size());
			printTotalCounts(counts);
		});

		all.join();

	}

	private static void printTotalCounts(Map<Integer, AtomicInteger> counts) {
		synchronized (counts) {

			StringBuilder result = new StringBuilder();

			for (int i = 1; i <= 14; i++) {

				int count = counts.getOrDefault(i, new AtomicInteger(0)).get();
				result.append(i).append("=").append(count).append(", ");
			}

			if (result.length() > 0) {
				result.setLength(result.length());
			}
			System.out.println(result);
		}
	}

	private static List<Integer> fetchDataChunk(int chunk, Assignment8 assignment8, Map<Integer, AtomicInteger> counts,
			ExecutorService exService) {
		int chunkSize = 1000;
		int start = chunk * chunkSize;
		int end = start + chunkSize;

		List<Integer> dataChunk = IntStream.range(start, end).boxed().map(n -> assignment8.getNumbers(n, n + 1))
				.flatMap(List::stream).limit(10).map(i -> {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					counts.computeIfAbsent(i, k -> new AtomicInteger()).incrementAndGet();
					return i;
				}).collect(Collectors.toList());

		return dataChunk;

	}

	public class EmptyAssignment8 extends Assignment8 {

		@Test
		public void testGetEmptyNumbers() {
			EmptyAssignment8 emptyAssignment8 = new EmptyAssignment8();

			List<Integer> result = emptyAssignment8.getNumbers();

			assertTrue(result.isEmpty());
		}

		@Override
		public List<Integer> getNumbers() {
			return getEmptyNumbers();
		}

	}
}
