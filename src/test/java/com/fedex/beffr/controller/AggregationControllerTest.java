package com.fedex.beffr.controller;

import com.fedex.beffr.prototype.AggregatorDTOMock;
import com.fedex.beffr.dto.AggregateDTO;
import com.fedex.beffr.service.BackendServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

class AggregationControllerTest {


    private final BackendServiceImpl backendServiceMock = Mockito.mock(BackendServiceImpl.class);
    private final AggregationController aggregationControllerUnderTest = new AggregationController(backendServiceMock);


    @Test
    void aggregate() {
        final List<String> pricing = List.of("DE", "BE", "JP", "NL", "BR");
        final List<String> track = List.of("12345", "23456", "34567", "45678");
        final List<String> shipments = List.of("12345789", "23456789", "3456789", "456789");

        doReturn(AggregatorDTOMock.getMock())
                .when(backendServiceMock)
                .aggregate(eq(pricing), eq(track), eq(shipments));

        final AggregateDTO result = assertDoesNotThrow(() -> aggregationControllerUnderTest.aggregate(pricing, track, shipments));
        final List<String> pricingResult = new ArrayList<>(result.getPricing().keySet());
        final List<String> shipmentsResult = new ArrayList<>(result.getShipments().keySet());
        final List<String> trackResult = new ArrayList<>(result.getTrack().keySet());

        assertTrue(pricingResult.containsAll(pricing));
        assertTrue(shipmentsResult.containsAll(shipments));
        assertTrue(trackResult.containsAll(track));
    }
}