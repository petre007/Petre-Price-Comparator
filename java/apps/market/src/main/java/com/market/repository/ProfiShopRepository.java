package com.market.repository;

import com.market.model.ProfiShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfiShopRepository extends JpaRepository<ProfiShop, Long> {
}
