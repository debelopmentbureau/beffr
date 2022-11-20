package com.fedex.beffr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShipmentsDTO {
    private Map<String, List<String>> shipment;
}
