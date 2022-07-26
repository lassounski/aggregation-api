package com.tnt.aggregationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregationResponse {
    private Map<String, Double> pricing;
    private Map<String, String> tracking;
    private Map<String, List<String>> shipping;
}
