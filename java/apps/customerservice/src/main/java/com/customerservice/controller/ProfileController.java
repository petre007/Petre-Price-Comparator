package com.customerservice.controller;

import com.customerservice.model.Customer;
import com.customerservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(value = "/profile/{username}")
    public ResponseEntity<Customer> getCustomerByUsername(
            @PathVariable String username
    ) throws Exception {
        return ResponseEntity.ok(this.profileService.getCustomerByUsername(username));
    }

}
