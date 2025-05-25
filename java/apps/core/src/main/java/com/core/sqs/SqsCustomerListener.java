package com.core.sqs;

import com.core.service.CustomerService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsCustomerListener {

    private final CustomerService customerService;

    @SqsListener(value = "${sqs.customers}")
    public void listen(String message) {
        customerService.processMessage(message);
    }

}
