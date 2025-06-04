package com.blm.takeout.repository;

import com.blm.takeout.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Page<Favorite> findByUser_useridOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.userid = :userId AND f.targetType = :targetType ORDER BY f.createdAt DESC")
    Page<Favorite> findByUserIdAndTargetTypeOrderByCreatedAtDesc(
            @Param("userId") Integer userId,
            @Param("targetType") Favorite.TargetType targetType,
            Pageable pageable);
            
    void deleteByUser_useridAndTargetTypeAndTargetId(Integer userId, Favorite.TargetType targetType, Integer targetId);
    
    boolean existsByUser_useridAndTargetTypeAndTargetId(Integer userId, Favorite.TargetType targetType, Integer targetId);
} 