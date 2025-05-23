package com.core.dto;

import com.core.model.ProductModel;
import org.springframework.data.domain.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class ProductPageResponse {

    private Page<ProductModel> page;
    private Map<String, AttributeValue> lastEvaluatedKey;

    public ProductPageResponse(Page<ProductModel> page, Map<String, AttributeValue> lastEvaluatedKey) {
        this.page = page;
        this.lastEvaluatedKey = lastEvaluatedKey;
    }

    public Page<ProductModel> getPage() {
        return page;
    }

    public Map<String, AttributeValue> getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

}
