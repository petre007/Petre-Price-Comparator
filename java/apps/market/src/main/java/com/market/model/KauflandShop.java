package com.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "kaufland")
@Getter
@NoArgsConstructor
@SuperBuilder
public class KauflandShop extends ShopProduct {
}
