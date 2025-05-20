package com.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "profi")
@Getter
@NoArgsConstructor
@SuperBuilder
public class ProfiShop extends ShopProduct {
}
