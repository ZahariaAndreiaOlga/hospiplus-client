package org.hospi.hospiplusclient.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Convert JSON string to a Java object (e.g., Map or custom class)
    public static <T> T parseJson(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
