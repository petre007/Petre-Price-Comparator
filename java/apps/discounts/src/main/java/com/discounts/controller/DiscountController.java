package com.discounts.controller;

import com.discounts.dto.DiscountDTO;
import com.discounts.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/discount")
    public ResponseEntity<String> addDiscountToProduct(@RequestBody DiscountDTO discountDTO) {
        this.discountService.createDiscount(discountDTO);
        return ResponseEntity.ok("Successfully added");
    }

}
