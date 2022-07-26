package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericRequestQueueTest {

    private ClientConfiguration clientConfiguration = mock(ClientConfiguration.class);

    private GenericRequestQueue requestQueue = new GenericRequestQueue(clientConfiguration);

    @BeforeEach
    void setup() {
        when(clientConfiguration.getBulkSize()).thenReturn(Integer.valueOf(5));
    }

    @Test
    void shouldRegisterRequests_NoDuplicates() {
        requestQueue.registerRequests(List.of("BR","PL","IT"));
        requestQueue.registerRequests(List.of("BR","PL","RU","NL"));

        Queue queue = requestQueue.getRequestQueue();

        assertThat(queue).contains("BR","PL","IT","RU","NL");
    }

    @Test
    void shouldHaveEnoughElementsToRequest() {
        requestQueue.registerRequests(List.of("BR","PL","IT","RU","NL"));

        requestQueue.getRequestQueue();

        assertThat(requestQueue.hasEnoughElementsToRequest()).isTrue();
    }
    
    @Test
    void shouldDrainTheQueue() {
        requestQueue.registerRequests(List.of("BR","PL","IT","RU","NL"));

        requestQueue.getRequestQueue();

        assertThat(requestQueue.drainQueue(5)).contains("BR","PL","IT","RU","NL");
    }

    @Test
    void shouldDrainTheQueue_OnEmptiness() {
        requestQueue.registerRequests(List.of("BR","PL","IT"));

        requestQueue.getRequestQueue();

        assertThat(requestQueue.drainQueue(5)).contains("BR","PL","IT");
    }

    @Test
    void shouldRegisterResponseAndCheckIt() {
        requestQueue.registerResponse(Map.of("NL",20.123, "PL", 40.131));

        assertThat(requestQueue.hasResponseFor("NL")).isTrue();
        assertThat(requestQueue.tryMatchKey("NL")).isEqualTo(20.123);
    }

    @SneakyThrows
    @Test
    void shouldCleanUpResponseForKey() {
        requestQueue.registerResponse(Map.of("NL",20.123, "PL", 40.131));

        requestQueue.scheduleToCleanResponseFor("PL");

        await().until(() -> requestQueue.getResponseMap().size() == 1);

        assertThat(requestQueue.getResponseMap()).containsEntry("NL",20.123);
    }
}
