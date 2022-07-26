package com.tnt.aggregationservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnt.aggregationservice.exception.ApiError;
import com.tnt.aggregationservice.model.AggregationResponse;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.List;

import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_1;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_2;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_3;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_4;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_5;
import static com.tnt.aggregationservice.TestData.AGGREGATION_ID_6;
import static com.tnt.aggregationservice.TestData.EN;
import static com.tnt.aggregationservice.TestData.US;
import static com.tnt.aggregationservice.TestData.FR;
import static com.tnt.aggregationservice.TestData.ES;
import static com.tnt.aggregationservice.TestData.NL;
import static com.tnt.aggregationservice.TestData.RU;
import static org.assertj.core.api.Assertions.assertThat;

class AggregationIT {

    private static final String GET_AGGREGATION_URL = "/aggregation";
    private static final String PRICING_PARAM = "countryNames";
    private static final String TRACKING_PARAM = "trackingNumbers";
    private static final String SHIPPING_PARAM = "shippingNumbers";

    private WebTestClient webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:8081")
            .responseTimeout(Duration.ofSeconds(15))
            .build();

    private ObjectMapper jsonObjectMapper = new ObjectMapper();

    private StopWatch stopWatch = new StopWatch();

    @Test
    void shouldRequestMoreItemsThanThrottleCapacity() {
        var prices1 = List.of(NL, EN, US, FR);
        var trackIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);
        var shipmentIds1 = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);
        var prices2 = List.of(ES, RU);
        var trackIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);
        var shipmentIds2 = List.of(AGGREGATION_ID_5, AGGREGATION_ID_6);

        stopWatch.start();
        AggregationResponse aggregationResponse1 = makeAggregationRequest(prices1, trackIds1, shipmentIds1);
        AggregationResponse aggregationResponse2 = makeAggregationRequest(prices2, trackIds2, shipmentIds2);
        stopWatch.stop();

        assertThat(aggregationResponse1.getPricing()).hasSize(4);
        assertThat(aggregationResponse2.getPricing()).hasSize(2);
        assertThat(stopWatch.getTotalTimeSeconds()).isCloseTo(10, Percentage.withPercentage(90));
    }


    @Test
    void shouldSuccessfullyCompleteWithOneParameter() {
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2, AGGREGATION_ID_3, AGGREGATION_ID_4);

        AggregationResponse response = makeAggregationRequest(null, trackIds, null);

        assertThat(response.getPricing()).isEmpty();
        assertThat(response.getShipping()).isEmpty();
        assertThat(response.getTracking()).containsKeys(
                AGGREGATION_ID_1,
                AGGREGATION_ID_2,
                AGGREGATION_ID_3,
                AGGREGATION_ID_4);
    }

    @Test
    void shouldGetResultAfterTimeout_whenInputParamsAreLessThenBulkSize() {
        var prices = List.of(NL, EN);
        var trackIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2);
        var shipmentIds = List.of(AGGREGATION_ID_1, AGGREGATION_ID_2);

        stopWatch.start();
        AggregationResponse response = makeAggregationRequest(prices, trackIds, shipmentIds);
        stopWatch.stop();

        assertThat(response.getPricing()).containsKeys(NL, EN);
        assertThat(response.getTracking()).containsKeys(AGGREGATION_ID_1, AGGREGATION_ID_2);
        assertThat(response.getShipping()).containsKeys(AGGREGATION_ID_1, AGGREGATION_ID_2);
        assertThat(stopWatch.getTotalTimeSeconds()).isCloseTo(5, Percentage.withPercentage(90));
    }

    private AggregationResponse makeAggregationRequest(List<String> prices1, List<String> trackIds1, List<String> shipmentIds1) {
        AggregationResponse aggregationResponse = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, prices1)
                        .queryParam(TRACKING_PARAM, trackIds1)
                        .queryParam(SHIPPING_PARAM, shipmentIds1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AggregationResponse.class)
                .returnResult().getResponseBody();
        return aggregationResponse;
    }
    
    @Test
    void shouldValidateInput() {
        ApiError apiError = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_AGGREGATION_URL)
                        .queryParam(PRICING_PARAM, List.of("NLD","R"))
                        .queryParam(TRACKING_PARAM, List.of("123","1234567890"))
                        .queryParam(SHIPPING_PARAM, List.of("123","1234567890"))
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiError.class)
                .returnResult().getResponseBody();

        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getErrors()).contains(
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.shippingNumbers[1].<list element>: length must be between 9 and 9",
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.trackingNumbers[0].<list element>: length must be between 9 and 9",
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.countryNames[1].<list element>: length must be between 2 and 2",
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.countryNames[0].<list element>: length must be between 2 and 2",
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.trackingNumbers[1].<list element>: length must be between 9 and 9",
                "com.tnt.aggregationservice.controller.AggregationController getAggregation.shippingNumbers[0].<list element>: length must be between 9 and 9"
        );
    }
}
