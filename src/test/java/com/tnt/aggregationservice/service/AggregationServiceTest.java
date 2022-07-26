package com.tnt.aggregationservice.service;

import com.tnt.aggregationservice.model.AggregationResponse;
import com.tnt.aggregationservice.model.PricingResult;
import com.tnt.aggregationservice.model.ShippingResult;
import com.tnt.aggregationservice.model.TrackingResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_1;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_2;
import static com.tnt.aggregationservice.TestData.EN;
import static com.tnt.aggregationservice.TestData.ES;
import static com.tnt.aggregationservice.TestData.NL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AggregationServiceTest {

    private PricingResult pricingResult = Mockito.mock(PricingResult.class);
    private TrackingResult trackingResult = Mockito.mock(TrackingResult.class);
    private ShippingResult shippingResult = Mockito.mock(ShippingResult.class);
    private QueryService pricingService = Mockito.mock(QueryService.class);
    private QueryService trackingService = Mockito.mock(QueryService.class);
    private QueryService shippingService = Mockito.mock(QueryService.class);

    private AggregationService aggregationService = new AggregationService(pricingService,trackingService,shippingService);

    @Test
    void shouldAggregateResults() {
        when(pricingService.getQueryResult(any(List.class)))
                .thenReturn(pricingResult);
        when(trackingService.getQueryResult(any(List.class)))
                .thenReturn(trackingResult);
        when(shippingService.getQueryResult(any(List.class)))
                .thenReturn(shippingResult);

        Mono<AggregationResponse> aggregationResponse = aggregationService.aggregate(
                List.of(NL,ES,EN),
                List.of(AGGREGATION_ID_1, AGGREGATION_ID_2),
                List.of(AGGREGATION_ID_1, AGGREGATION_ID_2)
        );

        StepVerifier.create(aggregationResponse)
                .expectNextMatches(aggregationResponse1 ->
                                aggregationResponse1.getPricing().equals(pricingResult) &&
                                aggregationResponse1.getTracking().equals(trackingResult) &&
                                aggregationResponse1.getShipping().equals(shippingResult)
                        );
    }
}
