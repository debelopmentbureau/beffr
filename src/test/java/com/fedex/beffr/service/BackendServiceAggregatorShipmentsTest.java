package com.fedex.beffr.service;

import com.fedex.beffr.client.BackendServicesClient;
import com.fedex.beffr.config.properties.AggregationConfigProperties;
import com.fedex.beffr.dto.ShipmentsDTO;
import com.fedex.beffr.prototype.ShipmentsDTOMock;
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

class BackendServiceAggregatorShipmentsTest extends BackendServiceAggregatorBaseTest {

    private final BackendServicesClient client = Mockito.mock(BackendServicesClient.class);
    private final AggregationConfigProperties properties = Mockito.mock(AggregationConfigProperties.class);
    private final BackendServiceAggregatorShipments shipmentAggregatorUnderTest = new BackendServiceAggregatorShipments(client, properties);

    @BeforeEach
    void setUp() {
        doReturn(_5_SEC)
                .when(properties)
                .getTimerLimitMilliseconds();
        doReturn(LIMIT_OF_5_REQUESTS)
                .when(properties)
                .getQueueLimit();
        doReturn(ShipmentsDTOMock.getMock())
                .when(client)
                .getShipments(anySet());
    }

    @ParameterizedTest
    @MethodSource("getReachedQueueLimitData")
    void aggregatedRequest_reachedQueueLimit(int numberOfRequests, int numberOfExecutionTimes) throws InterruptedException {
        final BackendServiceAggregatorShipments aggregator = new BackendServiceAggregatorShipments(client, properties);
        this.commonAggregatedRequestReachedQueueLimit(aggregator, numberOfRequests);
        verify(client, times(numberOfExecutionTimes)).getShipments(anySet());
    }

    @Test
    void getAggregatedRequest_reachedTimeLimit() throws InterruptedException {
        BackendServiceAggregatorShipments aggregator = new BackendServiceAggregatorShipments(client, properties);
        this.commonGetAggregatedRequestReachedTimeLimit(aggregator);
        verify(client, times(1)).getShipments(anySet());
    }

    @Test
    void aggregatedRequest_exception() {
        doThrow(new RestClientException("mockException"))
                .when(client)
                .getShipments(anySet());
        final ShipmentsDTO shipmentsDTO = assertDoesNotThrow(() -> shipmentAggregatorUnderTest.aggregatedRequest(List.of("567")));
        assertNull(shipmentsDTO.getShipment());
    }
}