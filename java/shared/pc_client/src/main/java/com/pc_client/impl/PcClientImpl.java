package com.pc_client.impl;

import com.pc_client.client.WebClientFactory;
import com.pc_client.model.Product;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class PcClientImpl {

    private final WebClient webClient;

    public PcClientImpl(String baseUrl, String apiKey) {
        this.webClient = WebClientFactory.create(baseUrl, apiKey);
    }

    public Mono<Product> getProductById(Long productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(Product.class);
    }

}
