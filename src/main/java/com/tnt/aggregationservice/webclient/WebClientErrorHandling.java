package com.tnt.aggregationservice.webclient;

import com.tnt.aggregationservice.exception.AggregationServiceException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class WebClientErrorHandling {
    public static  Mono<? extends Throwable> errorHandler(ClientResponse clientResponse) {
        return Mono.error(new AggregationServiceException("Error during retrieval of data from 3rd party API. Status: " + clientResponse.statusCode()));
    }

    public static Throwable mapError(Throwable e) {
        return new AggregationServiceException(" Fetching failed", e);
    }
}
