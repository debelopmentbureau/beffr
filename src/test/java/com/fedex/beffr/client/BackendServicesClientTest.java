package com.fedex.beffr.client;

import com.fedex.beffr.config.properties.BackendServicesProperties;
import com.fedex.beffr.dto.PricingDTO;
import com.fedex.beffr.dto.ShipmentsDTO;
import com.fedex.beffr.dto.TrackDTO;
import com.fedex.beffr.prototype.PricingDTOMock;
import com.fedex.beffr.prototype.ShipmentsDTOMock;
import com.fedex.beffr.prototype.TrackDTOMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

class BackendServicesClientTest {
    private final BackendServicesProperties properties = Mockito.mock(BackendServicesProperties.class);
    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final BackendServicesClient clientUnderTest = new BackendServicesClient(properties, restTemplate);

    private final Set<String> pricing = Set.of("DE", "BE", "JP", "NL", "BR");
    private final Set<String> track = Set.of("12345", "23456", "45678");
    private final Set<String> shipment = Set.of("12345789", "23456789", "3456789", "456789");


    @BeforeEach
    void setUp() {
        doReturn("http://localhost:8080")
                .when(properties)
                .getUrl();
    }

    @Test
    void getPricing() {
        doReturn("/pricing")
                .when(properties)
                .getPricingPath();

        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getPricingPath())
                .queryParam("q", pricing)
                .build()
                .toUri();

        final Map<String, Double> pricing = PricingDTOMock.getMock().getPricing();
        doReturn(pricing)
                .when(restTemplate)
                .getForObject(eq(uri), eq(Object.class));

        final PricingDTO pricingDTO = assertDoesNotThrow(() -> clientUnderTest.getPricing(this.pricing));
        assertEquals(pricingDTO.getPricing(), pricing);
    }

    @Test
    void getShipments() {
        doReturn("/shipment")
                .when(properties)
                .getShipmentsPath();

        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getShipmentsPath())
                .queryParam("q", this.shipment)
                .build()
                .toUri();

        final Map<String, List<String>> shipment = ShipmentsDTOMock.getMock().getShipment();
        doReturn(shipment)
                .when(restTemplate)
                .getForObject(eq(uri), eq(Object.class));

        final ShipmentsDTO shipmentsDTO = assertDoesNotThrow(() -> clientUnderTest.getShipments(this.shipment));
        assertEquals(shipmentsDTO.getShipment(), shipmentsDTO.getShipment());
    }

    @Test
    void getTrack() {
        doReturn("/track")
                .when(properties)
                .getTrackPath();

        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getTrackPath())
                .queryParam("q", track)
                .build()
                .toUri();

        final Map<String, String> track = TrackDTOMock.getMock().getTrack();
        doReturn(track)
                .when(restTemplate)
                .getForObject(eq(uri), eq(Object.class));

        final TrackDTO trackDTO = assertDoesNotThrow(() -> clientUnderTest.getTrack(this.track));
        assertEquals(trackDTO.getTrack(), trackDTO.getTrack());
    }

}