package com.fedex.beffr.service;

import com.fedex.beffr.dto.AggregateDTO;

import java.util.List;

public interface BackendService {
    /**
     * @param pricingList   price request
     * @param trackList     track request
     * @param shipmentsList track request
     * @return aggregated requests
     */
    AggregateDTO aggregate(List<String> pricingList, List<String> trackList, List<String> shipmentsList);
}
