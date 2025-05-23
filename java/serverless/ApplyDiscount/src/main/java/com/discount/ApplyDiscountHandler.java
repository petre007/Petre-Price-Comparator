package com.discount;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;

;

public class ApplyDiscountHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb = DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1) // Change to your region
            .build();

    private final String TABLE_NAME = "PriceCatalogDynamoDb";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!input.containsKey("product_id") || !input.containsKey("shop") ||
                    !input.containsKey("discount_percentage") || !input.containsKey("discount_expiry_date")) {
                response.put("statusCode", 400);
                response.put("body", "Missing required fields.");
                return response;
            }

            Long productId = Long.parseLong(input.get("product_id").toString());
            String shop = input.get("shop").toString();
            Double discountPercentage = Double.parseDouble(input.get("discount_percentage").toString());
            String discountExpiryDate = input.get("discount_expiry_date").toString();

            // Construct key using product_id as Number
            Map<String, AttributeValue> key = Map.of(
                    "product_id", AttributeValue.fromN(productId.toString()),
                    "shop", AttributeValue.fromS(shop)
            );

            // Check if item exists
            GetItemRequest getRequest = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            GetItemResponse getResponse = dynamoDb.getItem(getRequest);

            if (!getResponse.hasItem()) {
                response.put("statusCode", 404);
                response.put("body", "Product not found.");
                return response;
            }

            // Update the item
            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .updateExpression("SET discount_percentage = :dp, discount_expiry_date = :de")
                    .expressionAttributeValues(Map.of(
                            ":dp", AttributeValue.fromN(discountPercentage.toString()),
                            ":de", AttributeValue.fromS(discountExpiryDate)
                    ))
                    .build();

            dynamoDb.updateItem(updateRequest);

            response.put("statusCode", 200);
            response.put("body", "Discount fields updated successfully.");
            return response;

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "Error: " + e.getMessage());
            return response;
        }
    }
}
