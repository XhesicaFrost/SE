package com.blm.takeout.repository;

import com.blm.takeout.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    
    @Query("SELECT r FROM Recommendation r WHERE r.userId = :userId ORDER BY r.score DESC")
    List<Recommendation> findTopRecommendationsByUserId(@Param("userId") Integer userId);
    
    List<Recommendation> findByUserId(Integer userId);
    
    void deleteByUserId(Integer userId);
} 