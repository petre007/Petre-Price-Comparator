package com.core.dto;

import com.core.model.ProductModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class BasketDto {
    private Set<ProductModel> items;
    private Double totalAmount;
}
