package com.blm.takeout.service;

import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.BrowseHistory.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrowseHistoryService {
    BrowseHistory addBrowseHistory(Integer userId, TargetType targetType, String targetId, String name, String image, String description);
    Page<BrowseHistory> getUserBrowseHistory(Integer userId, Pageable pageable);
    Page<BrowseHistory> getUserBrowseHistoryByType(Integer userId, TargetType targetType, Pageable pageable);
    void deleteBrowseHistory(Integer userId, TargetType targetType, String targetId);
} 