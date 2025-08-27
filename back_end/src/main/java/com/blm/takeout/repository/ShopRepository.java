package com.blm.takeout.repository;

import com.blm.takeout.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByType(String type);
    List<Shop> findByIsOpen(Boolean isOpen);
    List<Shop> findByRatingGreaterThanEqual(Double minRating);
    Page<Shop> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<Shop> findByNameContaining(String keyword, Pageable pageable);
    
    @Query("SELECT s FROM Shop s WHERE s.id IN (SELECT o.shop.id FROM Order o WHERE o.user.userid = :userId ORDER BY o.createdAt DESC)")
    List<Shop> findShopsByUserOrders(@Param("userId") Integer userId);
    
    @Query(value = "SELECT * FROM shop ORDER BY rating DESC LIMIT 10", nativeQuery = true)
    List<Shop> findTop10ByOrderByRatingDesc();
    
    Shop findByUserId(Integer userId);
    
    List<Shop> findTop10ByStatusOrderByRatingDesc(Shop.Status status);
} 