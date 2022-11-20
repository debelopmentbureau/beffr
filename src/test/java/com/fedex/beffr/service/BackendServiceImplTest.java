package com.fedex.beffr.service;

import com.fedex.beffr.dto.AggregateDTO;
import com.fedex.beffr.exception.BadRequestException;
import com.fedex.beffr.prototype.PricingDTOMock;
import com.fedex.beffr.prototype.ShipmentsDTOMock;
import com.fedex.beffr.prototype.TrackDTOMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;

class BackendServiceImplTest {

    final BackendServiceAggregatorTrack mockAggregatorTrack = Mockito.mock(BackendServiceAggregatorTrack.class);
    final BackendServiceAggregatorPricing mockAggregatorPricing = Mockito.mock(BackendServiceAggregatorPricing.class);
    final BackendServiceAggregatorShipments mockAggregatorShipments = Mockito.mock(BackendServiceAggregatorShipments.class);
    final BackendService backendServiceUnderTest = new BackendServiceImpl(mockAggregatorTrack, mockAggregatorPricing, mockAggregatorShipments);
    private final List<String> price = List.of("DE", "BE", "JP", "NL", "BR");
    private final List<String> track = List.of("12345", "23456", "45678");
    private final List<String> shipment = List.of("12345789", "23456789", "3456789", "456789");


    @ParameterizedTest
    @MethodSource("getGuardClauseTestData")
    void aggregate_guardClause(List<String> price, List<String> shipping, List<String> track) {
        assertThrows(BadRequestException.class, () -> backendServiceUnderTest.aggregate(price, track, shipping));
    }

    @Test
    void aggregate() {
        doReturn(TrackDTOMock.getMock())
                .when(mockAggregatorTrack)
                .aggregatedRequest(anyList());

        doReturn(ShipmentsDTOMock.getMock())
                .when(mockAggregatorShipments)
                .aggregatedRequest(anyList());

        doReturn(PricingDTOMock.getMock())
                .when(mockAggregatorPricing)
                .aggregatedRequest(anyList());

        final AggregateDTO result = assertDoesNotThrow(() -> backendServiceUnderTest.aggregate(price, track, shipment));
        assertTrue(result.getPricing().keySet().containsAll(price));
        assertTrue(result.getTrack().keySet().containsAll(track));
        assertTrue(result.getShipments().keySet().containsAll(shipment));
    }


    private static Stream<Arguments> getGuardClauseTestData() {
        return Stream.of(
                Arguments.of(List.of("D", "ABE", "WRONG"), null, null),
                Arguments.of(null, List.of("!", "ASDF", "12345678.9", "1234578910", "asdf"),null),
                Arguments.of(null, null, List.of("!", "ASDF", "12345678.9", "1234578910", "asdf")));
    }


}