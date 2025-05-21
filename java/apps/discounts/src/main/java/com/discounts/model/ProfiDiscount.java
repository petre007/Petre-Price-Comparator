package com.discounts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "profi_discounts")
@Getter
@NoArgsConstructor
@SuperBuilder
public class ProfiDiscount extends DiscountProduct{
}
