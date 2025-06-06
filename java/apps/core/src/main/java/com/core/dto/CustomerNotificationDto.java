package com.core.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerNotificationDto {

    private Long customerId;
    private Long productId;
    private Double desiredPrice;

}
