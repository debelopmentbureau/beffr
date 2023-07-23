package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import com.fedex.beffr.dto.PricingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregated service for {@link PricingDTO}
 * Aggregate requests from multiple threads and dispatch the same result for all
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component("pricingAggregator")
@EnableConfigurationProperties(AggregationConfigProperties.class)
public class BackendServiceAggregatorPricing extends BackendServiceAggregator<PricingDTO> {
    private final PricingDTO pricingDTO = new PricingDTO();

    public BackendServiceAggregatorPricing(final BackendServicesClient client,
                                           final AggregationConfigProperties properties) {

        super(client, properties);
        final Thread thread = new Thread(this::waitUntilTimeLimitIsReached);
        thread.start();
    }

    /**
     * Add list request to the queue and wait until thread is notified by {@link BackendServiceAggregator#fetchDtoAndResumeProcess()}
     * <p>If the queue reached his limit, all requests will be forwarded and threads will be notified</p>
     *
     * @param pricingList pricingList request;
     * @return {@link PricingDTO}  result for all waiting requests
     */
    @Override
    public PricingDTO aggregatedRequest(final List<String> pricingList) {
        queue.add(pricingList);
        synchronized (this.lockObject) {
            waitUntilQueueLimitIsReached();
            return this.pricingDTO;
        }
    }


    @Override
    protected void fetchDtoAndResumeProcess() {
        try {
            final boolean isNotEmptyQueue = !queue.isEmpty();
            if (isNotEmptyQueue) {
                final PricingDTO pricing = this.client.getPricing(queue.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toSet()));
                this.pricingDTO.setPricing(pricing.getPricing());
            }
        } catch (Exception e) {
            log.error(">> Error during fetch track data", e);
            pricingDTO.setPricing(null);
        } finally {
            clearQueue();
            resetCounter();
            this.lockObject.notifyAll();
        }
    }

}
