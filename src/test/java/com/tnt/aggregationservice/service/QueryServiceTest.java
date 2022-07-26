package com.tnt.aggregationservice.service;

import com.tnt.aggregationservice.config.ClientConfiguration;
import com.tnt.aggregationservice.model.TrackingResult;
import com.tnt.aggregationservice.queue.GenericRequestQueue;
import com.tnt.aggregationservice.webclient.NonBlockingClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.tnt.aggregationservice.TestData.getAggregationIds;
import static com.tnt.aggregationservice.TestData.getCopyOfTrackMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class QueryServiceTest {

    private final static Map<String, String> TRACKING_RESULT = getCopyOfTrackMap();
    private ClientConfiguration clientConfiguration = org.mockito.Mockito.mock(ClientConfiguration.class);

    private NonBlockingClient nonBlockingClient = Mockito.mock(NonBlockingClient.class);

    private GenericRequestQueue requestQueue = new GenericRequestQueue(clientConfiguration);

    private QueryService<TrackingResult> queryService = new QueryService<>(clientConfiguration,
            nonBlockingClient,
            requestQueue,
            () -> new TrackingResult());

    @Test
    void shouldGetTrackingResultsFromSlowService() {
        Mono<Map<String, String>> slowResponse = Mono.just(TRACKING_RESULT);

        when(clientConfiguration.getBulkSize()).thenReturn(5);
        when(nonBlockingClient.fetch(any(List.class)))
                .thenReturn(slowResponse);

        TrackingResult trackingResult = await()
                .until(() -> queryService.getQueryResult(getAggregationIds()),
                        TrackingResult::isCompleted
                );

        assertThat(trackingResult.getResultMap()).isEqualTo(TRACKING_RESULT);
    }

}
