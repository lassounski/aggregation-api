package com.tnt.aggregationservice.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class NonBlockingClient <T>{

    public static final String QUERY_PARAM = "q";
    private final String url;
    private final WebClient webClient;

    public Mono<Map<String,T>> fetch(List<String> items) {

        if (items.isEmpty()) {
            return Mono.just(new HashMap());
        }

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam(QUERY_PARAM, items)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientErrorHandling::errorHandler)
                .bodyToMono(new ParameterizedTypeReference<Map<String,T>>() {})
                .onErrorMap(WebClientErrorHandling::mapError)
                .onErrorReturn(returnOnError(items));
    }

    private Map returnOnError(List<String> keys){
        var result = new HashMap<String, String>();
        keys.forEach(k ->  result.put(k, null));
        return result;
    }
}
