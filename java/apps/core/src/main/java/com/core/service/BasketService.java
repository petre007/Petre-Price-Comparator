package com.core.service;

import com.core.dto.BasketDto;
import com.core.model.ProductModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final ProductCatalogService productCatalogService;
    private final RedisService redisService;

    public void addProductIntoBasket(Long customerId, Long productId)
            throws Exception {
        ProductModel productModel = this.productCatalogService
                .findLowestPricedProduct(productId)
                .orElseThrow(() ->  new Exception("Product with product_id " + productId + " is not registered in the Product Catalog"));

        this.redisService.addProduct(customerId.toString(), productModel);

    }

    public BasketDto getBasket(Long customerId) {
        Set<ProductModel> productModelSet = this.redisService.getProducts(customerId.toString());
        return BasketDto.builder()
                .items(productModelSet)
                .totalAmount(productModelSet.stream()
                        .map(ProductModel::getPrice)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum())
                .build();
    }


}
