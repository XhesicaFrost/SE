package com.blm.takeout.repository;

import com.blm.takeout.entity.ShopRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ShopRecommendationRepository extends JpaRepository<ShopRecommendation, Integer> {
    
    @Query("SELECT sr FROM ShopRecommendation sr WHERE sr.userId = :userId ORDER BY sr.score DESC")
    List<ShopRecommendation> findTopRecommendationsByUserId(@Param("userId") Integer userId);
    
    List<ShopRecommendation> findByUserId(Integer userId);
    
    void deleteByUserId(Integer userId);
} 