package com.core.service;

import com.core.dto.CustomerSnsDto;
import com.core.dto.CustomerNotificationDto;
import com.core.model.CustomerModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final DynamoDbTable<CustomerModel> table;
    private final ObjectMapper objectMapper;

    public void processMessage(String message) {
        try {
            // 1. Deserialize JSON into DTO
            CustomerSnsDto dto = objectMapper.readValue(message, CustomerSnsDto.class);
            log.info("Deserialized DTO: {}", dto);

            // 2. Map DTO to DynamoDB entity
            CustomerModel model = new CustomerModel();
            model.setCustomerId(dto.customerId());
            model.setPhoneNumber(dto.phoneNumber());
            model.setProductId(0L);

            // 3. Put into DynamoDB table
            table.putItem(model);

            log.info("Stored in DynamoDB customer: {}", model.getCustomerId());

        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }

    public void setDesiredPriceForProduct(CustomerNotificationDto customerNotificationDto) {
        Long customerId = customerNotificationDto.getCustomerId();
        Long targetProductId = customerNotificationDto.getProductId();
        Double desiredPrice = customerNotificationDto.getDesiredPrice();

        CustomerModel defaultItem = table.getItem(
                Key.builder()
                        .partitionValue(customerId)
                        .sortValue(0L)
                        .build()
        );

        if (defaultItem == null) {
            log.warn("Default item with product_id=0 not found for customerId={}", customerId);
            return;
        }

        CustomerModel newItem = new CustomerModel();
        newItem.setCustomerId(customerId);
        newItem.setProductId(targetProductId);
        newItem.setPhoneNumber(defaultItem.getPhoneNumber());
        newItem.setDesiredPrice(desiredPrice);

        table.putItem(newItem);
        log.info("Inserted new item for customerId={}, productId={}, desiredPrice={}",
                customerId, targetProductId, desiredPrice);
    }

}
