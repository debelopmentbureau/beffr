package com.fedex.beffr.prototype;

import com.fedex.beffr.dto.PricingDTO;

import java.util.Map;

public class PricingDTOMock {
    private static final Map<String, Double> pricing = Map.of(
            "NL", 83.71659354073077D,
            "BR", 63.96750563341852D,
            "JP", 34.130296724012446D,
            "BE", 17.71659354750563D,
            "DE", 12.29672404073077D);

    public static PricingDTO getMock() {
        return PricingDTO.builder().pricing(pricing).build();
    }
}
