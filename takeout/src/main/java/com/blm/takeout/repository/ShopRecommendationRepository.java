package com.blm.takeout.repository;

import com.blm.takeout.entity.ShopRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShopRecommendationRepository extends JpaRepository<ShopRecommendation, Integer> {
    List<ShopRecommendation> findByUser_useridOrderByScoreDesc(Integer userId);
    void deleteByUser_userid(Integer userId);
} 