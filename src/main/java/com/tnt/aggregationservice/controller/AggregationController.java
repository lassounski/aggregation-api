package com.tnt.aggregationservice.controller;

import com.tnt.aggregationservice.model.AggregationResponse;
import com.tnt.aggregationservice.service.AggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class AggregationController {

    private final AggregationService aggregationService;

    @GetMapping(path = "/aggregation")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AggregationResponse> getAggregation(@RequestParam("countryNames") Optional<List<@Length(min = 2, max = 2) String>> countryNames,
                                                    @RequestParam("trackingNumbers")  Optional<List<@Length(min = 9, max = 9) String>> trackingNumbers,
                                                    @RequestParam("shippingNumbers")  Optional<List<@Length(min = 9, max = 9) String>> shippingNumbers){
        log.info("Requesting aggregation with countryNames[{}], trackingNumbers[{}] and shippingNumbers[{}]",
                countryNames, trackingNumbers, shippingNumbers);
        return aggregationService.aggregate(
                        countryNames.orElse(List.of()),
                        trackingNumbers.orElse(List.of()),
                        shippingNumbers.orElse(List.of()));
    }
}
