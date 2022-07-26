package com.tnt.aggregationservice.service;

import com.tnt.aggregationservice.config.ClientConfiguration;
import com.tnt.aggregationservice.exception.AggregationServiceException;
import com.tnt.aggregationservice.model.QueryResult;
import com.tnt.aggregationservice.queue.GenericRequestQueue;
import com.tnt.aggregationservice.webclient.NonBlockingClient;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class QueryService <T extends QueryResult> {

    private final ClientConfiguration config;
    private final NonBlockingClient<T> nonBlockingClient;
    private final GenericRequestQueue queue;
    private final Supplier<T> resultSupplier;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);

    public T getQueryResult(List<String> keys) {
        registerRequests(keys);
        return subscribeOnResponses(keys);
    }

    private void registerRequests(List<String> keys) {
        queue.registerRequests(keys);
        tryToRequest();
    }

    private T subscribeOnResponses(List<String> forKeys) {
        scheduleTimeoutRequest(config.getThrottledTimeout());
        return tryMatchResponses(forKeys);
    }

    private synchronized void tryToRequest() {
        if (queue.hasEnoughElementsToRequest()) {
            request(queue.drainQueue(config.getBulkSize()));
        }
    }

    private void request(List<String> keysToProcess) {
        nonBlockingClient.fetch(keysToProcess).subscribe(queue::registerResponse);
    }

    private void scheduleTimeoutRequest(int throttledTimeout) {
        scheduledExecutorService
                .schedule(() -> {
                    request(queue.drainQueue(config.getBulkSize()));
                }, throttledTimeout, TimeUnit.MILLISECONDS);
    }

    private T tryMatchResponses(List<String> keys) {
        T result = resultSupplier.get();

        result.registerForMatch(keys);

        while (!result.isCompleted()) {
            tryMatchResult(result);
            delayCheck();
        }
        return result;
    }

    private void tryMatchResult(T result) {
        Iterator<String> keyToMatch =  result.getKeysToMatch().iterator();
        while(keyToMatch.hasNext()) {
            String key = keyToMatch.next();
            if (queue.hasResponseFor(key)) {
                result.put(key, queue.tryMatchKey(key));
                queue.scheduleToCleanResponseFor(key);
            }
        }
    }

    private void delayCheck() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new AggregationServiceException("Delay response matching failure", e);
        }
    }
}
