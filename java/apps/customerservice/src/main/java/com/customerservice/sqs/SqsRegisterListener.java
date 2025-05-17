package com.customerservice.sqs;

import com.customerservice.service.SqsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsRegisterListener {

    private final SqsService sqsService;

    @SqsListener(value = "${sqs.name}")
    public void listen(String message) {
        try {
            sqsService.processMessage(message);
        } catch (JsonProcessingException e) {
            log.error("Couldn't process message");
        }

    }
}
