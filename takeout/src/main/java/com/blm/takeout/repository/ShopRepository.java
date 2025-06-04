package com.blm.takeout.repository;

import com.blm.takeout.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByType(String type);
    List<Shop> findByIsOpen(Boolean isOpen);
    List<Shop> findByRatingGreaterThanEqual(Double minRating);
} 