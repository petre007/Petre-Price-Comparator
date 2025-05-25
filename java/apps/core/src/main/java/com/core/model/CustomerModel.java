package com.core.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class CustomerModel {

    private Long customerId;
    private Long productId;
    private String phoneNumber;
    private Double desiredPrice;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "customer_id")
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "product_id")
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @DynamoDbAttribute(value = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @DynamoDbAttribute(value = "desired_price")
    public Double getDesiredPrice() {
        return desiredPrice;
    }

    public void setDesiredPrice(Double desiredPrice) {
        this.desiredPrice = desiredPrice;
    }
}
