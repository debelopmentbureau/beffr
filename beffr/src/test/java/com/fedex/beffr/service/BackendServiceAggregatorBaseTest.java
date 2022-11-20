package com.fedex.beffr.service;

import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BackendServiceAggregatorBaseTest {
    public static final long _5_SEC = 5000L;
    public static final long LIMIT_OF_5_REQUESTS = 5L;
    public void commonAggregatedRequestReachedQueueLimit(final BackendServiceAggregator<?> aggregator, int numberOfRequests) throws InterruptedException {
        List<List<String>> requests = new ArrayList<>();
        IntStream.range(0, numberOfRequests).forEach(request -> requests.add(List.of("1234")));

        CountDownLatch latch = new CountDownLatch(requests.size());
        ExecutorService service = newFixedThreadPool(requests.size());

        requests.forEach(request ->
                service.execute(() -> {
                    aggregator.aggregatedRequest(request);
                    latch.countDown();
                }));

        latch.await();
    }

    protected void commonGetAggregatedRequestReachedTimeLimit(BackendServiceAggregator<?> aggregator) throws InterruptedException {
        List<String> oneRequest = List.of("1234");
        CountDownLatch latch = new CountDownLatch(1);

        newFixedThreadPool(1).execute(() -> {
            aggregator.aggregatedRequest(oneRequest);
            latch.countDown();
        });

        assertTrue(latch.await(10L, TimeUnit.SECONDS));
    }

    protected static Stream<Arguments> getReachedQueueLimitData() {
        return Stream.of(
                Arguments.of(5, 1),
                Arguments.of(10, 2));
    }
}
