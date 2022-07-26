package com.tnt.aggregationservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestData {
    public static final String AGGREGATION_ID_1 = "298521438";
    public static final String AGGREGATION_ID_2 = "826502625";
    public static final String AGGREGATION_ID_3 = "728154894";
    public static final String AGGREGATION_ID_4 = "298521438";
    public static final String AGGREGATION_ID_5 = "613129783";
    public static final String AGGREGATION_ID_6 = "827991422";
    public static final String NL = "NL";
    public static final String EN = "EN";
    public static final String US = "US";
    public static final String FR = "FR";
    public static final String RU = "RU";
    public static final String ES = "ES";
    public static final String TRACK_ID_1 = "NEW";
    public static final String TRACK_ID_2 = "COLLECTING";
    public static final String TRACK_ID_3 = "COLLECTED";
    public static final String TRACK_ID_4 = "IN TRANSIT";
    public static final String TRACK_ID_5 = "DELIVERING";
    public static final String TRACK_ID_6 = "NEW";

    private static  final HashMap<String, String> trackMap = new HashMap<>();

    static {
        trackMap.put(AGGREGATION_ID_1, TRACK_ID_1);
        trackMap.put(AGGREGATION_ID_2, TRACK_ID_2);
        trackMap.put(AGGREGATION_ID_3, TRACK_ID_3);
        trackMap.put(AGGREGATION_ID_4, TRACK_ID_4);
        trackMap.put(AGGREGATION_ID_5, TRACK_ID_5);
        trackMap.put(AGGREGATION_ID_6, TRACK_ID_6);
    }

    public static Map<String,String> getCopyOfTrackMap() {
        return Map.copyOf(trackMap);
    }

    public static List<String> getAggregationIds() {
        return List.of(AGGREGATION_ID_1,
                AGGREGATION_ID_2,
                AGGREGATION_ID_3,
                AGGREGATION_ID_4,
                AGGREGATION_ID_5,
                AGGREGATION_ID_6);
    }
}
