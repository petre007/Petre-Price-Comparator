package com.core.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@DynamoDbBean
public class ProductModel {
    private Long productId;
    private String shop;
    private String brand;
    private String currency;
    private String packageQuantity;
    private String packageUnit;
    private Double price;
    private String productCategory;
    private String productName;
    private Double discountPercentage;
    private String discountExpiryDate;
    private String discountApplyDate;
    private String discountAddedDate;
    private transient Double valuePerUnit;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("product_id")
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @DynamoDbAttribute("shop")
    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    @DynamoDbAttribute("brand")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @DynamoDbAttribute("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @DynamoDbAttribute("package_quantity")
    public String getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(String packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    @DynamoDbAttribute("package_unit")
    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    @DynamoDbAttribute("price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @DynamoDbAttribute("product_category")
    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @DynamoDbAttribute("product_name")
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @DynamoDbAttribute("discount_percentage")
    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @DynamoDbAttribute("discount_expiry_date")
    public String getDiscountExpiryDate() {
        return discountExpiryDate;
    }

    public void setDiscountExpiryDate(String discountExpiryDate) {
        this.discountExpiryDate = discountExpiryDate;
    }

    @DynamoDbAttribute("discount_starting_date")
    public String getDiscountApplyDate() {
        return discountApplyDate;
    }

    public void setDiscountApplyDate(String discountApplyDate) {
        this.discountApplyDate = discountApplyDate;
    }

    @DynamoDbAttribute("discount_added_date")
    public String getDiscountAddedDate() {
        return discountAddedDate;
    }

    public void setDiscountAddedDate(String discountAddedDate) {
        this.discountAddedDate = discountAddedDate;
    }

    @DynamoDbIgnore
    public Double getValuePerUnit() {
        return valuePerUnit;
    }

    public void setValuePerUnit(Double valuePerUnit) {
        this.valuePerUnit = valuePerUnit;
    }

}
