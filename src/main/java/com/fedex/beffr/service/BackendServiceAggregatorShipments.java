package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import com.fedex.beffr.dto.ShipmentsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregated service for {@link ShipmentsDTO}
 * Aggregate requests from multiple threads and dispatch the same result for all
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component("shipmentsAggregator")
@EnableConfigurationProperties(AggregationConfigProperties.class)
@Slf4j
public class BackendServiceAggregatorShipments extends BackendServiceAggregator<ShipmentsDTO> {
    private final ShipmentsDTO shipmentsDTO = new ShipmentsDTO();
    ;

    public BackendServiceAggregatorShipments(final BackendServicesClient client,
                                             final AggregationConfigProperties properties) {
        super(client, properties);
        final Thread thread = new Thread(this::waitUntilTimeLimitIsReached);
        thread.start();
    }

    /**
     * Add list request to the queue and wait until thread is notified by {@link BackendServiceAggregator#fetchDtoAndResumeProcess()}
     * <p>If the queue reached his limit, all requests will be forwarded and threads will be notified </p>
     *
     * @param shipmentsList shipmentsList Request
     * @return {@link ShipmentsDTO} result for all waiting requests
     */
    @Override
    public ShipmentsDTO aggregatedRequest(List<String> shipmentsList) {
        queue.add(shipmentsList);
        synchronized (this.lockObject) {
            waitUntilQueueLimitIsReached();
            return this.shipmentsDTO;
        }
    }


    @Override
    protected void fetchDtoAndResumeProcess() {
        try {
            final boolean isNotEmptyQueue = !queue.isEmpty();
            if (isNotEmptyQueue) {
                final ShipmentsDTO shipments = this.client.getShipments(queue.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toSet()));
                this.shipmentsDTO.setShipment(shipments.getShipment());
            }
        } catch (Exception e) {
            log.error(">> Error during fetch track data", e);
            shipmentsDTO.setShipment(null);
        } finally {
            clearQueue();
            resetCounter();
            this.lockObject.notifyAll();
        }
    }
}
