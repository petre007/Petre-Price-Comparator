package com.core.controller;

import com.core.dto.AddToBasketReqBody;
import com.core.dto.BasketDto;
import com.core.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping(value = "/basket")
    public ResponseEntity<String> addToBasket(
            @RequestBody AddToBasketReqBody addToBasketReqBody
    ) throws Exception {
        this.basketService.addProductIntoBasket(addToBasketReqBody.customerId(), addToBasketReqBody.productId());
        return ResponseEntity.ok("Successfully added");
    }

    @GetMapping(value = "/basket/{customerId}",  produces = "application/json")
    public ResponseEntity<BasketDto> getBasket(@PathVariable Long customerId) {
        return ResponseEntity.ok(this.basketService.getBasket(customerId));
    }

}
