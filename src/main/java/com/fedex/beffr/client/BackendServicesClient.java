package com.fedex.beffr.client;

import com.fedex.beffr.config.properties.BackendServicesProperties;
import com.fedex.beffr.dto.PricingDTO;
import com.fedex.beffr.dto.ShipmentsDTO;
import com.fedex.beffr.dto.TrackDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
@EnableConfigurationProperties(BackendServicesProperties.class)
public class BackendServicesClient {

    private final BackendServicesProperties properties;
    private final RestTemplate restTemplate;

    /**
     * Fetch pricing data from Backend-Service for given pricingList arguments
     *
     * @param pricingList query params for get pricing request
     * @return pricingList for given query params
     */
    public PricingDTO getPricing(final Set<String> pricingList) throws RestClientException {
        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getPricingPath())
                .queryParam("q", pricingList)
                .build()
                .toUri();
        log.info(">>getPricing {}", uri);
        final Map<String, Double> pricing = (Map<String, Double>) restTemplate.getForObject(uri, Object.class);
        return PricingDTO.builder()
                .pricing(pricing)
                .build();
    }

    /**
     * Fetch shipment data from Backend-Service for given shipmentList arguments
     *
     * @param shipmentList query params for get shipment request
     * @return shipmentList for given query params
     */
    public ShipmentsDTO getShipments(final Set<String> shipmentList) throws RestClientException {
        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getShipmentsPath())
                .queryParam("q", shipmentList)
                .build()
                .toUri();
        log.info(">>getShipments {}", uri);
        final Map<String, List<String>> shipment = (Map<String, List<String>>) restTemplate.getForObject(uri, Object.class);
        return ShipmentsDTO.builder()
                .shipment(shipment)
                .build();
    }

    /**
     * Fetch track data from Backend-Service for given trackList arguments
     *
     * @param trackList query params for get track request
     * @return trackList for given query params
     */
    public TrackDTO getTrack(final Set<String> trackList) throws RestClientException {
        final URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(properties.getTrackPath())
                .queryParam("q", trackList)
                .build()
                .toUri();
        log.info(">>getTrack {}", uri);
        final Map<String, String> track = (Map<String, String>) restTemplate.getForObject(uri, Object.class);
        return TrackDTO.builder()
                .track(track)
                .build();
    }
}

