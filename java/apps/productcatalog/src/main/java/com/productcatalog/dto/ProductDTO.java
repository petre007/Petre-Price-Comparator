package com.productcatalog.dto;

public record ProductDTO(
        String productName,
        String productCategory,
        String brand,
        String packageQuantity,
        String packageUnit
){}
