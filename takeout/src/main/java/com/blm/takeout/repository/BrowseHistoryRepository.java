package com.blm.takeout.repository;

import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.BrowseHistory.TargetType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {
    Page<BrowseHistory> findByUserId(Integer userId, Pageable pageable);
    Page<BrowseHistory> findByUserIdAndTargetType(Integer userId, TargetType targetType, Pageable pageable);
    void deleteByUserIdAndTargetTypeAndTargetId(Integer userId, TargetType targetType, String targetId);
    
    @Query("SELECT bh FROM BrowseHistory bh WHERE bh.userId = :userId AND bh.targetType = :targetType ORDER BY bh.browseTime DESC")
    Page<BrowseHistory> findByUserIdAndTargetTypeOrderByBrowseTimeDesc(
            @Param("userId") Integer userId,
            @Param("targetType") TargetType targetType,
            Pageable pageable);
} 