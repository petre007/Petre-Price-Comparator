package com.market.controller;

import com.market.dto.ShopDTO;
import com.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @PostMapping("/market")
    public ResponseEntity<String> addProductToShop(@RequestBody ShopDTO shopDTO) {
        this.marketService.addProductToShop(shopDTO);
        return ResponseEntity.ok("Successfully added");
    }

}
