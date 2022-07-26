package com.tnt.aggregationservice.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

public interface QueryResult <V>{

    void registerForMatch(List<String> track);

    boolean isCompleted();
    void put(String key, V value);

    ConcurrentSkipListSet<String> getKeysToMatch();

    Map<String, V> getResultMap();
}
