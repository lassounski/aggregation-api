package com.tnt.aggregationservice.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ShippingResult implements QueryResult<List<String>>{

    @Getter
    private ConcurrentSkipListSet<String> keysToMatch = new ConcurrentSkipListSet<>();

    @Getter
    private HashMap<String, List<String>> resultMap = new HashMap<>();

    public void registerForMatch(List<String> track) {
        keysToMatch.addAll(track);
    }

    public boolean isCompleted(){
        return keysToMatch.isEmpty();
    }

    public void put(String key, List<String> value){
        this.resultMap.put(key, value);
        keysToMatch.remove(key);
    }
}
