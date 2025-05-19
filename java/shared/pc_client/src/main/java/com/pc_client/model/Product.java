package com.pc_client.model;

import lombok.Data;

@Data
public class Product {

    private Long productId;
    private String productName;
    private String productCategory;
    private String brand;
    private String packageQuantity;
    private String packageUnit;

}
