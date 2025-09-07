package com.cpt.payments.service.redis.interfaces;

import java.util.List;
import java.util.Map;

public interface RedisService {

	void addValueToList(String key, String value);

    List<String> getAllValuesFromList(String key);

    // Hash operations
    void setValueInHash(String hashName, String key, String value);

    String getValueFromHash(String hashName, String key);

    Map<String, String> getAllEntriesFromHash(String hashName);

    // String (direct value) operations
    void setValue(String key, String value);

    void setValueWithExpiry(String key, String value, long timeoutInSecs);

    String getValue(String key);
}
