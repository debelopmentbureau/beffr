package com.fedex.beffr.prototype;

import com.fedex.beffr.dto.TrackDTO;

import java.util.Map;

public class TrackDTOMock {
    private static final Map<String, String> track = Map.of(
            "12345", "IN TRANSIT",
            "23456", "NEW",
            "34567", "NEW",
            "45678", "DELIVERED");

    public static TrackDTO getMock() {
        return TrackDTO.builder().track(track).build();
    }

}
