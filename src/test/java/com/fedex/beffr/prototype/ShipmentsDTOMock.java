package com.fedex.beffr.prototype;

import com.fedex.beffr.dto.ShipmentsDTO;

import java.util.List;
import java.util.Map;

public class ShipmentsDTOMock {
    private static final Map<String, List<String>> shipments = Map.of(
            "12345789", List.of("envelope", "pallet", "pallet", "box", "box", "envelope", "envelope", "box", "box"),
            "23456789", List.of("envelope", "pallet", "pallet", "box", "box", "envelope", "envelope", "box", "box"),
            "3456789", List.of("pallet", "envelope", "box", "pallet", "envelope", "box", "box", "box", "pallet"),
            "456789", List.of("pallet", "pallet", "box", "box", "envelope", "pallet", "box", "pallet", "envelope"));


    public static ShipmentsDTO getMock() {
        return ShipmentsDTO.builder().shipment(shipments).build();
    }

}
