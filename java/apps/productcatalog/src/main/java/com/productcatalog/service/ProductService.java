package com.productcatalog.service;

import com.productcatalog.dto.ProductDTO;
import com.productcatalog.dto.ProductResponseBody;
import com.productcatalog.model.Product;
import com.productcatalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    @Transactional
    public ProductResponseBody createProduct(ProductDTO product) {
        var productEntity = Product.builder()
                .brand(product.brand())
                .packageUnit(product.packageUnit())
                .productCategory(product.productCategory())
                .packageQuantity(product.packageQuantity())
                .productName(product.productName())
                .build();

        var response = this.productRepository.save(productEntity);

        return new ProductResponseBody(response.getProductId());
    }
}
