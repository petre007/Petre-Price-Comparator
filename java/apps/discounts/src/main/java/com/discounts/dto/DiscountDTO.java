package com.discounts.dto;

import com.discounts.model.Shops;

public record DiscountDTO(
   Long productId,
   Shops shop,
   String fromDate,
   String toDate,
   Double percentageOfDiscount
) {}
