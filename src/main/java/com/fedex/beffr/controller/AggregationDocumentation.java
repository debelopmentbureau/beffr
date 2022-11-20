package com.fedex.beffr.controller;

public class AggregationDocumentation {
    public static final String PRICING = "Query params for pricing.";
    public static final String SHIPMENTS = "Query params for pricing.";
    public static final String TRACK = "Query params for pricing.";
    static final String AGGREGATION_POST_SUMMARY = "Aggregate concurrent requests.";
    static final String AGGREGATION_POST_DESCRIPTION = """
            Aggregate concurrent requests,
            forward them when queue limit is reached or when time limit is reached
            and dispatch result according to each request individually.
            """;
}
