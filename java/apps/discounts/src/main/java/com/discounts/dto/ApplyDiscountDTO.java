package com.discounts.dto;

import com.discounts.model.Shops;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ApplyDiscountDTO {
    private Long productId;
    private Shops shop;
    private Double discountPercentage;
    private String expiryDate;
}
