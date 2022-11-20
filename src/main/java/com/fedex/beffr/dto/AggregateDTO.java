package com.fedex.beffr.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AggregateDTO {
    private Map<String, Double> pricing;
    private Map<String, List<String>> shipments;
    private Map<String, String> track;
}
