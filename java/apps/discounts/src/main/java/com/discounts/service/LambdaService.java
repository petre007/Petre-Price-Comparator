package com.discounts.service;

import com.discounts.dto.ApplyDiscountDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LambdaService {


    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String invokeUpdateDiscountLambda(ApplyDiscountDTO applyDiscountDTO)
            throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("product_id", applyDiscountDTO.getProductId());
        payload.put("shop", applyDiscountDTO.getShop());
        payload.put("discount_percentage", applyDiscountDTO.getDiscountPercentage());
        payload.put("discount_expiry_date", applyDiscountDTO.getExpiryDate());

        String jsonPayload = objectMapper.writeValueAsString(payload);

        InvokeRequest request = InvokeRequest.builder()
                .functionName("ApplyDiscount") // Name of your Lambda
                .payload(SdkBytes.fromString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        InvokeResponse response = lambdaClient.invoke(request);

        String responsePayload = response.payload().asUtf8String();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Lambda error: " + responsePayload);
        }

        return responsePayload;
    }
}
