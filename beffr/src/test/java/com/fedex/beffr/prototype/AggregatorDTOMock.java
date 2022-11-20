package com.fedex.beffr.prototype;

import com.fedex.beffr.dto.AggregateDTO;

public class AggregatorDTOMock {
    public static AggregateDTO getMock() {
        return AggregateDTO.builder()
                .track(TrackDTOMock.getMock().getTrack())
                .shipments(ShipmentsDTOMock.getMock().getShipment())
                .pricing(PricingDTOMock.getMock().getPricing())
                .build();
    }
}
