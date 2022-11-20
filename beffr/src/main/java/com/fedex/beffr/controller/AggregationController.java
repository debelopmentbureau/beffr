package com.fedex.beffr.controller;

import com.fedex.beffr.dto.AggregateDTO;
import com.fedex.beffr.service.BackendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fedex.beffr.controller.AggregationDocumentation.AGGREGATION_POST_DESCRIPTION;
import static com.fedex.beffr.controller.AggregationDocumentation.AGGREGATION_POST_SUMMARY;

@RestController
@RequestMapping("/aggregation")
@RequiredArgsConstructor
public class AggregationController {

    private final BackendService service;

    @Operation(summary = AGGREGATION_POST_SUMMARY,
            description = AGGREGATION_POST_DESCRIPTION)
    @GetMapping()
    public AggregateDTO aggregate(
            @Parameter(description = AggregationDocumentation.PRICING)
            @RequestParam(value = "pricing", required = false) final List<String> pricing,
            @Parameter(description = AggregationDocumentation.SHIPMENTS)
            @RequestParam(value = "track", required = false) final List<String> track,
            @Parameter(description = AggregationDocumentation.TRACK)
            @RequestParam(value = "shipments", required = false) final List<String> shipments) {
        return service.aggregate(pricing, track, shipments);
    }
}
