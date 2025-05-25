package com.core.controller;

import com.core.dto.CustomerNotificationDto;
import com.core.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerNotificationController {

    private final CustomerService customerService;

    @PostMapping("/customer")
    public ResponseEntity<String> setDesiredPriceForProduct(
            @RequestBody CustomerNotificationDto customerNotificationDto
    ) {
        this.customerService.setDesiredPriceForProduct(customerNotificationDto);
        return ResponseEntity.ok("Price successfully set");
    }

}
