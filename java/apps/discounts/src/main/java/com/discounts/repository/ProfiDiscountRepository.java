package com.discounts.repository;

import com.discounts.model.ProfiDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfiDiscountRepository extends JpaRepository<ProfiDiscount, Long> {
}
