package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GenericRequestQueue<ResponseType> {

    @Getter
    private final Queue<String> requestQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final HashMap<String, ResponseType> responseMap = new HashMap<>();
    private final ClientConfiguration config;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(60);

    public void registerRequests(List<String> keys) {
        var newKeys = keys.stream()
                .filter(key -> !requestQueue.contains(key))
                .collect(Collectors.toList());
        requestQueue.addAll(newKeys);
    }

    public boolean hasEnoughElementsToRequest() {
        return requestQueue.size() >= config.getBulkSize();
    }

    public synchronized List<String> drainQueue(int count) {
        List<String> result = new ArrayList<>();
        while (!requestQueue.isEmpty() && count != 0) {
            result.add(requestQueue.poll());
            count--;
        }
        return result;
    }

    public void registerResponse(Map<String, ResponseType> responseMap) {
        this.responseMap.putAll(responseMap);
    }

    public boolean hasResponseFor(String key) {
        return responseMap.containsKey(key);
    }

    public ResponseType tryMatchKey(String key) {
        return this.responseMap.get(key);
    }

    public void scheduleToCleanResponseFor(String key) {
        scheduledExecutorService.schedule(() -> responseMap.remove(key), 600, TimeUnit.MILLISECONDS);
    }
}
