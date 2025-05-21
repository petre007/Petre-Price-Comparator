package com.discounts.repository;

import com.discounts.model.LidlDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LidlDiscountRepository extends JpaRepository<LidlDiscount, Long> {
}
