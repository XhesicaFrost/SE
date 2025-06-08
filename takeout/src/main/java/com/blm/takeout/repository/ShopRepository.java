package com.blm.takeout.repository;

import com.blm.takeout.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByType(String type);
    List<Shop> findByIsOpen(Boolean isOpen);
    List<Shop> findByRatingGreaterThanEqual(Double minRating);
    Page<Shop> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<Shop> findByNameContaining(String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT s FROM Shop s WHERE s.id IN (SELECT o.shopId FROM Order o WHERE o.userId = :userId) ORDER BY s.id DESC")
    List<Shop> findShopsByUserOrders(@Param("userId") Integer userId);
    
    List<Shop> findTop10ByOrderByRatingDesc();
} 