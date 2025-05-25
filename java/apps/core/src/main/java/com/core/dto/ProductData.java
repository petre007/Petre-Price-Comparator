package com.core.dto;

public record ProductData(
        Long productId,
        String productName,
        Integer year,
        Integer month,
        Integer day,
        Double price
) {
}
