package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import com.fedex.beffr.dto.PricingDTO;
import com.fedex.beffr.prototype.PricingDTOMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

class BackendServiceAggregatorPricingTest extends BackendServiceAggregatorBaseTest {
    private final BackendServicesClient client = Mockito.mock(BackendServicesClient.class);
    private final AggregationConfigProperties properties = Mockito.mock(AggregationConfigProperties.class);
    private final BackendServiceAggregatorPricing pricingAggregatorUnderTest = new BackendServiceAggregatorPricing(client, properties);

    @BeforeEach
    void setUp() {
        doReturn(_5_SEC)
                .when(properties)
                .getTimerLimitMilliseconds();
        doReturn(LIMIT_OF_5_REQUESTS)
                .when(properties)
                .getQueueLimit();
        doReturn(PricingDTOMock.getMock())
                .when(client)
                .getPricing(anySet());
    }

    @ParameterizedTest
    @MethodSource("getReachedQueueLimitData")
    void aggregatedRequest_reachedQueueLimit(int numberOfRequests, int numberOfExecutionTimes) throws InterruptedException {
        BackendServiceAggregatorPricing aggregator = new BackendServiceAggregatorPricing(client, properties);
        this.commonAggregatedRequestReachedQueueLimit(aggregator, numberOfRequests);
        verify(client, times(numberOfExecutionTimes)).getPricing(anySet());
    }

    @Test
    void getAggregatedRequest_reachedTimeLimit() throws InterruptedException {
        BackendServiceAggregatorPricing aggregator = new BackendServiceAggregatorPricing(client, properties);
        this.commonGetAggregatedRequestReachedTimeLimit(aggregator);
        verify(client, times(1)).getPricing(anySet());
    }

    @Test
    void aggregatedRequest_exception() {
        doThrow(new RestClientException("mockException"))
                .when(client)
                .getPricing(anySet());
        final PricingDTO pricingDTO = assertDoesNotThrow(() -> pricingAggregatorUnderTest.aggregatedRequest(List.of("BR")));
        assertNull(pricingDTO.getPricing());
    }
}