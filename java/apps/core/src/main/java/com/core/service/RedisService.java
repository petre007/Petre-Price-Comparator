package com.core.service;

import com.core.model.ProductModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String PREFIX = "customer:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addProduct(String customerId, ProductModel product) {
        try {
            String productJson = objectMapper.writeValueAsString(product);
            redisTemplate.opsForSet().add(PREFIX + customerId, productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize product", e);
        }
    }

    public Set<ProductModel> getProducts(String customerId) {
        Set<String> jsonSet = redisTemplate.opsForSet().members(PREFIX + customerId);
        return jsonSet.stream().map(json -> {
            try {
                return objectMapper.readValue(json, ProductModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize product JSON", e);
            }
        }).collect(Collectors.toSet());
    }

    public void removeProduct(String customerId, ProductModel product) {
        try {
            String productJson = objectMapper.writeValueAsString(product);
            redisTemplate.opsForSet().remove(PREFIX + customerId, productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize product", e);
        }
    }

    public void clearProducts(String customerId) {
        redisTemplate.delete(PREFIX + customerId);
    }
}
