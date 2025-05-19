package com.productcatalog.controller;

import com.productcatalog.dto.ProductDTO;
import com.productcatalog.dto.ProductResponseBody;
import com.productcatalog.model.Product;
import com.productcatalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/product/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                this.productService.getProductById(id)
        );
    }

    @PostMapping(value = "/product")
    public ResponseEntity<ProductResponseBody> createProduct(
            @RequestBody ProductDTO productDTO
    ) {
        return ResponseEntity.ok(
                this.productService.createProduct(productDTO)
        );
    }

}
