package com.market.repository;

import com.market.model.LidlShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LidlShopRepository extends JpaRepository<LidlShop, Long> {
}
