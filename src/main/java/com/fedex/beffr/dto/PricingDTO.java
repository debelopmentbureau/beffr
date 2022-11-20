package com.fedex.beffr.dto;

import lombok.*;

import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PricingDTO {
    private Map<String, Double> pricing;
}
