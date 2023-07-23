package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public abstract class BackendServiceAggregator<T> {
    protected final ConcurrentLinkedQueue<List<String>> queue = new ConcurrentLinkedQueue<>();

    protected final BackendServicesClient client;
    protected final Object lockObject = new Object();
    protected final long queueLimit;
    protected final long timerLimit;
    protected long counter = System.currentTimeMillis();

    public BackendServiceAggregator(final BackendServicesClient client,
                                    final AggregationConfigProperties properties) {
        this.client = client;
        this.queueLimit = properties.getQueueLimit();
        this.timerLimit = properties.getTimerLimitMilliseconds();
    }

    protected void waitUntilQueueLimitIsReached() {
        synchronized (this.lockObject) {
            if (queue.size() >= queueLimit) {
                fetchDtoAndResumeProcess();
            } else {
                try {
                    lockObject.wait();
                } catch (InterruptedException e) {
                    log.error(">> Thread Interrupted Exception", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void waitUntilTimeLimitIsReached() {
        while (true) {
            if (System.currentTimeMillis() - counter >= timerLimit) {
                synchronized (this.lockObject) {
                    fetchDtoAndResumeProcess();
                }
            }
        }
    }

    protected void clearQueue() {
        queue.clear();
    }

    protected void resetCounter() {
        this.counter = System.currentTimeMillis();
    }

    abstract T aggregatedRequest(List<String> queryList);

    abstract void fetchDtoAndResumeProcess();
}