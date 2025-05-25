package com.core.service;

import com.core.dto.ProductData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.Datum;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AthenaService {

    private final AthenaClient athenaClient;

    @Value("${athena.database}")
    private String DATABASE;
    @Value("${athena.output.s3_bucket}")
    private String OUTPUT_BUCKET;

    public List<ProductData> queryProductCatalog(String brand, String category, String shop) {
        String query = String.format("""
            SELECT product_id, product_name, year, month, day, price
            FROM product_catalog
            WHERE brand = '%s'
              AND product_category = '%s'
              AND shop = '%s'
            ORDER BY product_id, year, month, day
            """, brand, category, shop);

        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryString(query)
                .queryExecutionContext(QueryExecutionContext.builder().database(DATABASE).build())
                .resultConfiguration(ResultConfiguration.builder().outputLocation(OUTPUT_BUCKET).build())
                .build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);
        String queryExecutionId = startQueryExecutionResponse.queryExecutionId();

        waitForQueryToComplete(queryExecutionId);

        return getQueryResults(queryExecutionId);
    }

    public List<ProductData> getQueryResults(String queryExecutionId) {
        List<ProductData> products = new ArrayList<>();

        GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                .queryExecutionId(queryExecutionId)
                .build();

        GetQueryResultsResponse getQueryResultsResponse = athenaClient.getQueryResults(getQueryResultsRequest);
        List<Row> rows = getQueryResultsResponse.resultSet().rows();

        // Skip header row
        for (int i = 1; i < rows.size(); i++) {
            List<Datum> data = rows.get(i).data();
            try {
                ProductData product = new ProductData(
                        Long.parseLong(data.get(0).varCharValue()),                        // product_id
                        data.get(1).varCharValue(),                        // product_name
                        Integer.parseInt(data.get(2).varCharValue()),      // year
                        Integer.parseInt(data.get(3).varCharValue()),      // month
                        Integer.parseInt(data.get(4).varCharValue()),      // day
                        Double.parseDouble(data.get(5).varCharValue())     // price
                );
                products.add(product);
            } catch (Exception e) {
                // Log error or skip invalid rows
            }
        }

        return products;
    }


    private void waitForQueryToComplete(String queryExecutionId) {
        boolean isQueryStillRunning = true;
        while (isQueryStillRunning) {
            GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                    .queryExecutionId(queryExecutionId)
                    .build();
            GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            QueryExecutionState state = getQueryExecutionResponse.queryExecution().status().state();

            switch (state) {
                case SUCCEEDED -> isQueryStillRunning = false;
                case FAILED, CANCELLED -> throw new RuntimeException("Athena query failed: " + state);
                default -> {}
            }
        }
    }
}
