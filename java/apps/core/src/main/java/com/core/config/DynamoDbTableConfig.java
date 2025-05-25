package com.core.config;

import com.core.model.CustomerModel;
import com.core.model.ProductModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbTableConfig {

    @Value("${price_catalog_dynamodb_table}")
    private String tableName;

    @Value("${customers_dynamodb_table}")
    private String customersTableName;

    @Bean
    public DynamoDbTable<ProductModel> productCatalogTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(tableName, TableSchema.fromBean(ProductModel.class));
    }

    @Bean
    public DynamoDbTable<CustomerModel> customerModelDynamoDbTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(customersTableName, TableSchema.fromBean(CustomerModel.class));
    }
}
