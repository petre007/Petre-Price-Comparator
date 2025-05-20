package com.market.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class ShopProduct {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "package_quantity")
    private String packageQuantity;

    @Column(name = "package_unit")
    private String packageUnit;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

}
