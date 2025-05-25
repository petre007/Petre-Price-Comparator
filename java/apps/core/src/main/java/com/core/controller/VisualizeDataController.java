package com.core.controller;

import com.core.dto.ProductData;
import com.core.service.AthenaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VisualizeDataController {

    private final AthenaService athenaService;

    @GetMapping(value = "/visualize_data")
    public ResponseEntity<List<ProductData>> getData(
            @RequestParam String shop,
            @RequestParam("product_category") String productCategory,
            @RequestParam String brand
    ) {
        return ResponseEntity.ok(this.athenaService.queryProductCatalog(brand, productCategory, shop));
    }

}
