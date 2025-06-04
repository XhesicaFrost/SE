package com.blm.takeout.repository;

import com.blm.takeout.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.targetType = :targetType ORDER BY f.createdAt DESC")
    Page<Favorite> findByUserIdAndTargetTypeOrderByCreatedAtDesc(
            @Param("userId") Integer userId,
            @Param("targetType") Favorite.TargetType targetType,
            Pageable pageable);
            
    void deleteByUserIdAndTargetTypeAndTargetId(Integer userId, Favorite.TargetType targetType, Integer targetId);
    
    boolean existsByUserIdAndTargetTypeAndTargetId(Integer userId, Favorite.TargetType targetType, Integer targetId);
} 