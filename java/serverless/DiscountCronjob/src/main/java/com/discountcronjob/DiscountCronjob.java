package com.discountcronjob;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscountCronjob implements RequestHandler<Object, String> {

    private static final String TABLE_NAME = "ProductCatalogDynamoDb";
    private static final String PARTITION_KEY = "product_id";

    @Override
    public String handleRequest(Object input, Context context) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(TABLE_NAME)
                    .build();

            ScanResponse scanResponse = dynamoDb.scan(scanRequest);

            for (Map<String, AttributeValue> item : scanResponse.items()) {
                if (shouldRevertDiscount(item)) {
                    revertDiscount(item, dynamoDb, context);
                }
            }

            return "Discount check completed.";
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean shouldRevertDiscount(Map<String, AttributeValue> item) {
        if (!item.containsKey("discount_expiry_date")) return false;

        String expiry = item.get("discount_expiry_date").s();
        LocalDate expiryDate = LocalDate.parse(expiry);
        return expiryDate.isBefore(LocalDate.now());
    }

    private void revertDiscount(Map<String, AttributeValue> item, DynamoDbClient dynamoDb, Context context) {
        try {
            String productId = item.get(PARTITION_KEY).s();
            double discountedPrice = Double.parseDouble(item.get("price").n());
            double discountPercent = Double.parseDouble(item.get("discount_percentage").n());

            double originalPrice = discountedPrice / (1 - discountPercent / 100.0);
            context.getLogger().log("Reverting discount for product " + productId +
                    ": " + discountedPrice + " → " + originalPrice);

            Map<String, AttributeValueUpdate> updates = new HashMap<>();
            updates.put("price", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().n(String.format("%.2f", originalPrice)).build())
                    .action(AttributeAction.PUT).build());

            // Nullify discount fields
            for (String field : List.of("discount_added_date", "discount_expiry_date",
                    "discount_percentage", "discount_starting_date")) {
                updates.put(field, AttributeValueUpdate.builder()
                        .action(AttributeAction.DELETE)
                        .build());
            }

            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(Collections.singletonMap(PARTITION_KEY, item.get(PARTITION_KEY)))
                    .attributeUpdates(updates)
                    .build();

            dynamoDb.updateItem(updateRequest);
        } catch (Exception e) {
            context.getLogger().log("Failed to revert product: " + e.getMessage());
        }
    }
}
