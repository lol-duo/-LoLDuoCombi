package com.lolduo.duo.service.temp;

import java.util.HashMap;
import java.util.Map;

public class PrimaryPerkMap {
    private static Map<Long, Long> primaryMap;

    public static Map<Long, Long> getPrimaryMap() {
        if (primaryMap == null) {
            primaryMap = new HashMap<>();

            primaryMap.put(8112L,8100L);
            primaryMap.put(8124L,8100L);
            primaryMap.put(8128L,8100L);
            primaryMap.put(9923L,8100L);

            primaryMap.put(8351L,8300L);
            primaryMap.put(8360L,8300L);
            primaryMap.put(8369L,8300L);

            primaryMap.put(8005L,8000L);
            primaryMap.put(8008L,8000L);
            primaryMap.put(8021L,8000L);
            primaryMap.put(8010L,8000L);

            primaryMap.put(8437L,8400L);
            primaryMap.put(8439L,8400L);
            primaryMap.put(8465L,8400L);

            primaryMap.put(8214L,8200L);
            primaryMap.put(8229L,8200L);
            primaryMap.put(8230L,8200L);
        }
        return primaryMap;
    }

    public static void freeMap() {
        primaryMap = null;
    }
}