package org.hospi.hospiplusclient.services;

import org.hospi.hospiplusclient.utils.ServiceResponse;
import org.hospi.hospiplusclient.utils.TokenStorage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/api/v1";


    private static HttpRequest.Builder createAuthorizedRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));

        // Add Authorization header if token is available
        String token = TokenStorage.getJwtToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return builder;
    }


    // GET Request
    public static CompletableFuture<ServiceResponse> sendGetRequest(String path){
        String finalUrl = BASE_URL + "/" + path;
        HttpRequest request = createAuthorizedRequest(finalUrl)
                .GET()
                .build();
        return sendRequest(request);
    }

    // GET Request
    public static CompletableFuture<ServiceResponse> sendGetRequest(String path, int id){
        String finalUrl = BASE_URL + "/" + path + "/" + id;
        HttpRequest request = createAuthorizedRequest(finalUrl)
                .GET()
                .build();
        return sendRequest(request);
    }

    // POST Request
    public static CompletableFuture<ServiceResponse> sendPostRequest(String path, String jsonPayload) {
        String finalUrl = BASE_URL + "/" + path;
        HttpRequest request = createAuthorizedRequest(finalUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return sendRequest(request);
    }

    // PUT Request
    public static CompletableFuture<ServiceResponse> sendPutRequest(String path, int id, String jsonPayload) {
        String finalUrl = BASE_URL + "/" + path + "/" + id;
        HttpRequest request = createAuthorizedRequest(finalUrl)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return sendRequest(request);
    }

    // DELETE Request
    public static CompletableFuture<ServiceResponse> sendDeleteRequest(String path, int id) {
        String finalUrl = BASE_URL + "/" + path + "/" + id;
        HttpRequest request = createAuthorizedRequest(finalUrl)
                .DELETE()
                .build();

        return sendRequest(request);
    }

    private static CompletableFuture<ServiceResponse> sendRequest(HttpRequest request) {
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new ServiceResponse(response.statusCode(), response.body()))
                .exceptionally(ex -> {
                    System.out.println("Error: " + ex.getMessage());
                    return new ServiceResponse(500, "Error: " + ex.getMessage());
                });
    }

}
