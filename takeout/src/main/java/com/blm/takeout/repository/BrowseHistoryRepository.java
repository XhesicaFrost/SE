package com.blm.takeout.repository;

import com.blm.takeout.entity.BrowseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Integer> {
    Page<BrowseHistory> findByUserIdOrderByBrowseTimeDesc(Integer userId, Pageable pageable);
    
    @Query("SELECT bh FROM BrowseHistory bh WHERE bh.user.id = :userId AND bh.targetType = :targetType ORDER BY bh.browseTime DESC")
    Page<BrowseHistory> findByUserIdAndTargetTypeOrderByBrowseTimeDesc(
            @Param("userId") Integer userId,
            @Param("targetType") BrowseHistory.TargetType targetType,
            Pageable pageable);
            
    void deleteByUserIdAndTargetTypeAndTargetId(Integer userId, BrowseHistory.TargetType targetType, Integer targetId);
} 