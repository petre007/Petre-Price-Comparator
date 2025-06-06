package com.core.service;

import com.core.dto.ProductPageResponse;
import com.core.model.ProductModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final DynamoDbTable<ProductModel> table;


    public ProductPageResponse getProducts(
            Optional<String> brand,
            Optional<String> shop,
            Optional<Double> minPrice,
            Optional<Double> maxPrice,
            int pageSize,
            Optional<String> lastKey) {

        List<String> expressions = new ArrayList<>();
        Map<String, AttributeValue> values = new HashMap<>();

        brand.ifPresent(val -> {
            expressions.add("brand = :brand");
            values.put(":brand", AttributeValue.fromS(val));
        });
        shop.ifPresent(val -> {
            expressions.add("shop = :shop");
            values.put(":shop", AttributeValue.fromS(val));
        });
        minPrice.ifPresent(val -> {
            expressions.add("price >= :minPrice");
            values.put(":minPrice", AttributeValue.fromN(val.toString()));
        });
        maxPrice.ifPresent(val -> {
            expressions.add("price <= :maxPrice");
            values.put(":maxPrice", AttributeValue.fromN(val.toString()));
        });

        Expression filterExpression = null;
        if (!expressions.isEmpty()) {
            filterExpression = Expression.builder()
                    .expression(String.join(" AND ", expressions))
                    .expressionValues(values)
                    .build();
        }

        ScanEnhancedRequest.Builder requestBuilder = ScanEnhancedRequest.builder().limit(pageSize);
        if (filterExpression != null) requestBuilder.filterExpression(filterExpression);
        lastKey.ifPresent(k -> requestBuilder.exclusiveStartKey(Map.of("product_id", AttributeValue.fromN(k)))); // use product_id not productId

        PageIterable<ProductModel> pages = table.scan(requestBuilder.build());
        software.amazon.awssdk.enhanced.dynamodb.model.Page<ProductModel> awsPage =
                pages.stream().findFirst().orElse(null);

        if (awsPage == null) {
            return new ProductPageResponse(Page.empty(), null);
        }

        List<ProductModel> items = awsPage.items();

        // ✅ Enrich each product with valuePerUnit
        items.forEach(this::computeAndSetValuePerUnit);

        Page<ProductModel> springPage = new PageImpl<>(items, PageRequest.of(0, pageSize), items.size());
        return new ProductPageResponse(springPage, awsPage.lastEvaluatedKey());
    }



    public Optional<ProductModel> findLowestPricedProduct(Long productId) {
        Key key = Key.builder().partitionValue(productId).build();

        return table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)))
                .stream()
                .flatMap(p -> p.items().stream())
                .filter(p -> p.getPrice() != null)
                .map(p -> {
                    double discount = Optional.ofNullable(p.getDiscountPercentage()).orElse(0.0);
                    double effectivePrice = p.getPrice() * (1 - discount / 100);
                    p.setPrice(effectivePrice); // Optionally override for clarity
                    return p;
                })
                .min(Comparator.comparingDouble(ProductModel::getPrice));
    }

    private void computeAndSetValuePerUnit(ProductModel product) {
        if (product.getPrice() == null || product.getPackageQuantity() == null || product.getPackageUnit() == null) {
            product.setValuePerUnit(null);
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(product.getPackageQuantity().toString());
        } catch (NumberFormatException e) {
            product.setValuePerUnit(null);
            return;
        }

        switch (product.getPackageUnit().toLowerCase()) {
            case "g": quantity /= 1000.0; break;
            case "ml": quantity /= 1000.0; break;
            case "kg":
            case "l": break;
            default:
                product.setValuePerUnit(null);
                return;
        }

        if (quantity > 0) {
            product.setValuePerUnit(product.getPrice() / quantity);
        } else {
            product.setValuePerUnit(null);
        }
    }


    public List<ProductModel> getRecentlyDiscountedProducts() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(24)).truncatedTo(ChronoUnit.MILLIS);

        Expression filter = Expression.builder()
                .expression("discount_added_date >= :cutoff")
                .expressionValues(Map.of(":cutoff", AttributeValue.fromS(cutoff.toString())))
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(filter)
                .build();

        return table.scan(request)
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());
    }

    public List<ProductModel> getTopDiscountedProducts(int limit) {
        Expression filter = Expression.builder()
                .expression("discount_percentage <> :null")
                .expressionValues(Map.of(":null", AttributeValue.fromN("0")))
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(filter)
                .build();

        return table.scan(request)
                .stream()
                .flatMap(page -> page.items().stream())
                .filter(p -> p.getDiscountPercentage() != null)
                .sorted(Comparator.comparing(ProductModel::getDiscountPercentage).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }


}
