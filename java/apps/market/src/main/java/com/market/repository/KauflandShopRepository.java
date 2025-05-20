package com.market.repository;

import com.market.model.KauflandShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KauflandShopRepository extends JpaRepository<KauflandShop, Long> {
}
