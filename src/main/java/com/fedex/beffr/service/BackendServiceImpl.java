package com.fedex.beffr.service;

import com.fedex.beffr.dto.AggregateDTO;
import com.fedex.beffr.dto.PricingDTO;
import com.fedex.beffr.dto.ShipmentsDTO;
import com.fedex.beffr.dto.TrackDTO;
import com.fedex.beffr.exception.BadRequestException;
import com.fedex.beffr.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class BackendServiceImpl implements BackendService {

    public static final String _9_DIGIT_NUMBER = "^\\d{1,9}";
    private static final HashSet<String> ISO2 = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
    private final BackendServiceAggregator<TrackDTO> trackAggregator;
    private final BackendServiceAggregator<PricingDTO> pricingAggregator;
    private final BackendServiceAggregator<ShipmentsDTO> shipmentsAggregator;

    public BackendServiceImpl(final BackendServiceAggregator<TrackDTO> trackAggregator,
                              final BackendServiceAggregator<PricingDTO> pricingAggregator,
                              final BackendServiceAggregator<ShipmentsDTO> shipmentsAggregator) {
        this.trackAggregator = trackAggregator;
        this.pricingAggregator = pricingAggregator;
        this.shipmentsAggregator = shipmentsAggregator;
    }

    /**
     * Cumulate request an aggregate them properly
     * <p>each request will run independently and provide result when ready</p>
     * <P>return result when all threads are done</P>
     *
     * @param priceList     price request
     * @param trackList     track request
     * @param shipmentsList track request
     * @return aggregated result
     */
    @Override
    public AggregateDTO aggregate(final List<String> priceList,
                                  final List<String> trackList,
                                  final List<String> shipmentsList) {

        guardClause(priceList, trackList, shipmentsList);
        final TrackDTO track = new TrackDTO();
        final PricingDTO pricing = new PricingDTO();
        final ShipmentsDTO shipments = new ShipmentsDTO();

        final Thread trackThread = getTrackThread(trackList, track);
        final Thread pricingThread = getPricingThread(priceList, pricing);
        final Thread shipmentsThread = getShipmentsThread(shipmentsList, shipments);
        try {
            trackThread.start();
            pricingThread.start();
            shipmentsThread.start();

            trackThread.join();
            pricingThread.join();
            shipmentsThread.join();

            return AggregateDTO.builder()
                    .pricing(pricing.getPricing())
                    .shipments(shipments.getShipment())
                    .track(track.getTrack())
                    .build();
        } catch (Exception e) {
            log.error(">> Exception during aggregate", e);
            throw new ServerException("Exception during aggregate");
        }
    }

    private void guardClause(final List<String> priceList, final List<String> trackList, final List<String> shipmentsList) {
        ofNullable(priceList)
                .flatMap(list -> list.stream()
                        .filter(iso2 -> !ISO2.contains(iso2))
                        .findFirst())
                .ifPresent(iso2 -> {
                    throw new BadRequestException("priceList code unrecognized: %s".formatted(iso2));
                });

        ofNullable(trackList)
                .flatMap(list -> list.stream()
                        .filter(track -> !track.matches(_9_DIGIT_NUMBER))
                        .findFirst())
                .ifPresent(track -> {
                    throw new BadRequestException("trackList element:%s is invalid".formatted(track));
                });

        ofNullable(shipmentsList)
                .flatMap(list -> list.stream()
                        .filter(shipment -> !shipment.matches(_9_DIGIT_NUMBER))
                        .findFirst())
                .ifPresent(shipment -> {
                    throw new BadRequestException("shipmentsList element:%s is invalid".formatted(shipment));
                });
    }

    private Thread getTrackThread(final List<String> trackList, final TrackDTO track) {
        return new Thread(() -> {
            if (!CollectionUtils.isEmpty(trackList)) {
                final TrackDTO trackDTO = trackAggregator.aggregatedRequest(trackList);
                ofNullable(trackDTO.getTrack())
                        .ifPresentOrElse(
                                result -> track.setTrack(result.entrySet()
                                        .stream()
                                        .filter(entry -> trackList.contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                                () -> track.setTrack(trackDTO.getTrack()));
            }
        });
    }

    private Thread getShipmentsThread(final List<String> shipmentsList, final ShipmentsDTO shipments) {
        return new Thread(() -> {
            if (!CollectionUtils.isEmpty(shipmentsList)) {
                final ShipmentsDTO shipmentsDTO = shipmentsAggregator.aggregatedRequest(shipmentsList);
                ofNullable(shipmentsDTO.getShipment())
                        .ifPresentOrElse(
                                result -> shipments.setShipment(result.entrySet()
                                        .stream()
                                        .filter(entry -> shipmentsList.contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                                () -> shipments.setShipment(shipmentsDTO.getShipment()));
            }
        });
    }

    private Thread getPricingThread(final List<String> priceList, final PricingDTO pricing) {
        return new Thread(() -> {
            if (!CollectionUtils.isEmpty(priceList)) {
                final PricingDTO pricingDTO = pricingAggregator.aggregatedRequest(priceList);
                ofNullable(pricingDTO.getPricing())
                        .ifPresentOrElse(
                                result -> pricing.setPricing(result.entrySet()
                                        .stream()
                                        .filter(entry -> priceList.contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                                () -> pricing.setPricing(pricingDTO.getPricing()));
            }
        });
    }
}

