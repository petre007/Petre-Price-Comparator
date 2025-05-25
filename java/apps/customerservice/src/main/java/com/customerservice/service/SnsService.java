package com.customerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService {

    private final SnsClient snsClient;
    @Value("${sns.customers-topic-arn}")
    private String topicArn;

    public void publishCustomerEvent(String messageBody, String messageGroupId) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(messageBody)
                .messageGroupId(messageGroupId)
                .build();

        PublishResponse response = snsClient.publish(request);
        log.info("Published message ID: " + response.messageId());
    }
}
