package com.discounts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "kaufland_discounts")
@Getter
@NoArgsConstructor
@SuperBuilder
public class KauflandDiscount extends DiscountProduct{
}
