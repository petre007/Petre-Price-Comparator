package com.market.dto;

import com.market.model.Currency;
import com.market.model.Shops;

public record ShopDTO(
        Long product_id,
        Shops shops,
        Double price,
        Currency currency
) {}
