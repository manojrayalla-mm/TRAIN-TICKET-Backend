package com.property.maintenance.dao;

import org.springframework.jdbc.support.KeyHolder;

import java.util.Map;

public final class DaoUtils {

    private DaoUtils() {
    }

    public static Long extractGeneratedId(KeyHolder keyHolder) {
        if (keyHolder == null) {
            return null;
        }
        if (keyHolder.getKeyList() != null && !keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            if (keys != null) {
                for (Map.Entry<String, Object> entry : keys.entrySet()) {
                    if ("id".equalsIgnoreCase(entry.getKey()) && entry.getValue() instanceof Number num) {
                        return num.longValue();
                    }
                }
                for (Object val : keys.values()) {
                    if (val instanceof Number num) {
                        return num.longValue();
                    }
                }
            }
        }
        try {
            Number key = keyHolder.getKey();
            return key != null ? key.longValue() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
