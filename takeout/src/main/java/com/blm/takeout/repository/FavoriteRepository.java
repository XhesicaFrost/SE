package com.blm.takeout.repository;

import com.blm.takeout.entity.Favorite;
import com.blm.takeout.entity.Favorite.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    Page<Favorite> findByUserIdAndTargetTypeOrderByCreatedAtDesc(Integer userId, TargetType targetType, Pageable pageable);
    boolean existsByUserIdAndTargetTypeAndTargetId(Integer userId, TargetType targetType, Integer targetId);
    void deleteByUserIdAndTargetTypeAndTargetId(Integer userId, TargetType targetType, Integer targetId);
} 