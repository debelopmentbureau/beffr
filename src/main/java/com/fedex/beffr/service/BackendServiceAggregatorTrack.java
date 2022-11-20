package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import com.fedex.beffr.dto.TrackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregated service for {@link TrackDTO}
 * Aggregate requests from multiple threads and dispatch the same result for all
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component("trackAggregator")
@EnableConfigurationProperties(AggregationConfigProperties.class)
@Slf4j
public class BackendServiceAggregatorTrack extends BackendServiceAggregator<TrackDTO> {
    private final TrackDTO trackDTO = new TrackDTO();

    public BackendServiceAggregatorTrack(final BackendServicesClient client,
                                         final AggregationConfigProperties properties) {
        super(client, properties);
        final Thread thread = new Thread(this::waitUntilTimeLimitIsReached);
        thread.start();
    }

    /**
     * Add list request to the queue and wait until thread is notified by {@link BackendServiceAggregator#fetchDtoAndResumeProcess()}
     * <p>If the queue reached his limit, all requests will be forwarded and threads will be notified </p>
     *
     * @param trackList trackList Request
     * @return {@link TrackDTO} result for all waiting requests
     */
    public TrackDTO aggregatedRequest(List<String> trackList) {
        queue.add(trackList);
        synchronized (this.lockObject) {
            waitUntilQueueLimitIsReached();
            return this.trackDTO;
        }
    }

    @Override
    protected void fetchDtoAndResumeProcess() {
        try {
            final boolean isNotEmptyQueue = !queue.isEmpty();
            if (isNotEmptyQueue) {
                final TrackDTO track = this.client.getTrack(queue.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toSet()));
                this.trackDTO.setTrack(track.getTrack());
            }
        } catch (Exception e) {
            log.error(">> Error during fetch track data", e);
            trackDTO.setTrack(null);
        } finally {
            clearQueue();
            resetCounter();
            this.lockObject.notifyAll();
        }
    }
}
