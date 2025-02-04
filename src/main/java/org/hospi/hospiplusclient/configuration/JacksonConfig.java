package org.hospi.hospiplusclient.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonConfig {
    public static ObjectMapper createObjectMapper() {
        // Create a new ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Register the JavaTimeModule to handle LocalDateTime and other Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());

        // Optional: Enable pretty-printing (optional)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }
}
