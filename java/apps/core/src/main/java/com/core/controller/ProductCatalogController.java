package com.core.controller;

import com.core.dto.ProductPageResponse;
import com.core.model.ProductModel;
import com.core.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;


    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam Optional<String> brand,
            @RequestParam Optional<String> shop,
            @RequestParam Optional<Double> minPrice,
            @RequestParam Optional<Double> maxPrice,
            @RequestParam Optional<Integer> pageSize,
            @RequestParam Optional<String> lastKey
    ) {
        ProductPageResponse result = productCatalogService.getProducts(
                brand, shop, minPrice, maxPrice, pageSize.orElse(20), lastKey
        );

        Map<String, Object> response = new HashMap<>();
        response.put("items", result.getPage().getContent());

        Map<String, AttributeValue> lastEvaluatedKey = result.getLastEvaluatedKey();
        if (lastEvaluatedKey != null && lastEvaluatedKey.containsKey("productId")) {
            response.put("nextKey", lastEvaluatedKey.get("productId").s());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/latest_discounts")
    public ResponseEntity<List<ProductModel>> getLatestDiscounts() {
        return ResponseEntity.ok(this.productCatalogService.getRecentlyDiscountedProducts());
    }
}
