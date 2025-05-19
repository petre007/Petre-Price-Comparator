package com.pc_client.client;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {

    /**
     * Creates a configured WebClient instance.
     *
     * @param baseUrl the base URL for the API
     * @param apiKey the API key for authorization
     * @return a configured WebClient instance
     */
    public static WebClient create(String baseUrl, String apiKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }

}