package com.tnt.aggregationservice.service;

import com.tnt.aggregationservice.model.AggregationResponse;
import com.tnt.aggregationservice.model.PricingResult;
import com.tnt.aggregationservice.model.ShippingResult;
import com.tnt.aggregationservice.model.TrackingResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;

import java.util.List;

@Service
@AllArgsConstructor
public class AggregationService {

    private final QueryService<PricingResult> pricingService;
    private final QueryService<TrackingResult> trackingService;
    private final QueryService<ShippingResult> shippingService;

    public Mono<AggregationResponse> aggregate(List<String> countryNames, List<String> trackingNumbers, List<String> shippingNumbers)  {
        Mono<PricingResult> pricingMono = Mono.defer(() -> Mono.just(pricingService.getQueryResult(countryNames)))
                .subscribeOn(Schedulers.boundedElastic());
        Mono<TrackingResult> trackMono = Mono.defer(() -> Mono.just(trackingService.getQueryResult(trackingNumbers)))
                .subscribeOn(Schedulers.boundedElastic());
        Mono<ShippingResult> shipmentMono = Mono.defer(() -> Mono.just(shippingService.getQueryResult(shippingNumbers)))
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(pricingMono, trackMono, shipmentMono)
                .map(resultTuple -> mapFetchResults(resultTuple));
    }

    private AggregationResponse mapFetchResults(Tuple3< PricingResult, TrackingResult, ShippingResult> results) {
        return AggregationResponse
                .builder()
                .pricing(results.getT1().getResultMap())
                .tracking(results.getT2().getResultMap())
                .shipping(results.getT3().getResultMap())
                .build();
    }
}
