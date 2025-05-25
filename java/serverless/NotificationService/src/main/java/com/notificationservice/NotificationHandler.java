package com.notificationservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHandler implements RequestHandler<DynamodbEvent, Void> {

    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();
    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
    private final String customersTable = System.getenv("CUSTOMERS_TABLE_NAME");

    @Override
    public Void handleRequest(DynamodbEvent event, Context context) {
        for (DynamodbEvent.DynamodbStreamRecord record : event.getRecords()) {
            if (!"INSERT".equals(record.getEventName()) && !"MODIFY".equals(record.getEventName())) {
                continue;
            }

            Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> newImage =
                    record.getDynamodb().getNewImage();

            if (newImage == null || !newImage.containsKey("product_id") || !newImage.containsKey("price")) {
                continue;
            }

            int productId = Integer.parseInt(newImage.get("product_id").getN());
            double newPrice = Double.parseDouble(newImage.get("price").getN());
            String productName = newImage.get("product_name").getS();
            String currency = newImage.get("currency").getS();

            notifyInterestedCustomers(productId, productName, currency, newPrice, context);
        }

        return null;
    }

    private void notifyInterestedCustomers(int productId, String productName, String currency, double newPrice, Context context) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":pid", new AttributeValue().withN(String.valueOf(productId)));

        QueryRequest query = new QueryRequest()
                .withTableName(customersTable)
                .withIndexName("product_id-index")  // 👈 Required for GSI
                .withKeyConditionExpression("product_id = :pid")
                .withExpressionAttributeValues(eav);

        try {
            QueryResult result = dynamoDb.query(query);
            List<Map<String, AttributeValue>> items = result.getItems();

            for (Map<String, AttributeValue> item : items) {
                double desiredPrice = Double.parseDouble(item.get("desired_price").getN());
                String phoneNumber = item.get("phone_number").getS();

                if (newPrice <= desiredPrice) {
                    sendSms(phoneNumber, productName, currency, newPrice, context);
                }
            }

        } catch (Exception e) {
            context.getLogger().log("Error querying customers table: " + e.getMessage());
        }
    }

    private void sendSms(String phoneNumber, String productName, String currency, double price, Context context) {
        String message = String.format("📢 Product %s is now %.2f %s. Buy it before the deal ends!", productName, price, currency);

        try {
            PublishRequest publishRequest = new PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(phoneNumber);

            snsClient.publish(publishRequest);
            context.getLogger().log("SMS sent to: " + phoneNumber);
        } catch (Exception e) {
            context.getLogger().log("Failed to send SMS to " + phoneNumber + ": " + e.getMessage());
        }
    }
}
