package com.discounts.repository;

import com.discounts.model.KauflandDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KauflandDiscountRepository extends JpaRepository<KauflandDiscount, Long> {
}
