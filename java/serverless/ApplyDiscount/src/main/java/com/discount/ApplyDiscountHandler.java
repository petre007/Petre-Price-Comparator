package com.discount;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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
                    !input.containsKey("discount_percentage") || !input.containsKey("discount_expiry_date") ||
                    !input.containsKey("discount_starting_date")) {
                response.put("statusCode", 400);
                response.put("body", "Missing required fields.");
                return response;
            }

            Long productId = Long.parseLong(input.get("product_id").toString());
            String shop = input.get("shop").toString();
            Double discountPercentage = Double.parseDouble(input.get("discount_percentage").toString());
            String discountExpiryDate = input.get("discount_expiry_date").toString();
            String discountApplyDate = input.get("discount_starting_date").toString();

            Map<String, AttributeValue> key = Map.of(
                    "product_id", AttributeValue.fromN(productId.toString()),
                    "shop", AttributeValue.fromS(shop)
            );

            // Retrieve item from DynamoDB
            GetItemResponse getResponse = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build());

            if (!getResponse.hasItem()) {
                response.put("statusCode", 404);
                response.put("body", "Product not found.");
                return response;
            }

            Map<String, AttributeValue> item = getResponse.item();

            if (!item.containsKey("price")) {
                response.put("statusCode", 422);
                response.put("body", "Product found but missing 'price' field.");
                return response;
            }

            // Calculate discounted price and overwrite `price`
            double originalPrice = Double.parseDouble(item.get("price").n());
            double discountedPrice = originalPrice * (1 - discountPercentage / 100.0);

            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .updateExpression("SET price = :newPrice, discount_percentage = :dp, " +
                            "discount_expiry_date = :de, discount_starting_date = :ds, " +
                            "discount_added_date = :da")
                    .expressionAttributeValues(Map.of(
                            ":newPrice", AttributeValue.fromN(String.format("%.2f", discountedPrice)),
                            ":dp", AttributeValue.fromN(discountPercentage.toString()),
                            ":de", AttributeValue.fromS(discountExpiryDate),
                            ":ds", AttributeValue.fromS(discountApplyDate),
                            ":da", AttributeValue.fromS(Instant.now().truncatedTo(ChronoUnit.MILLIS).toString())
                    ))
                    .build();

            dynamoDb.updateItem(updateRequest);

            response.put("statusCode", 200);
            response.put("body", String.format("Price updated from %.2f to %.2f", originalPrice, discountedPrice));
            return response;

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "Error: " + e.getMessage());
            return response;
        }
    }
}
