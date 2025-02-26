package org.hospi.hospiplusclient.utils;

public class ServiceResponse {
    private final int statusCode;
    private final String body;

    public ServiceResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
