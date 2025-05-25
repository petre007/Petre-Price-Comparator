package com.customerservice.dto;

public record CustomerSnsDto(
        Long customerId,
        String phoneNumber
) {
}
