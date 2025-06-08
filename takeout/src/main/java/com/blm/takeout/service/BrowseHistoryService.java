package com.blm.takeout.service;

import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.BrowseHistory.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrowseHistoryService {
    BrowseHistory addBrowseHistory(String userId, TargetType targetType, String targetId, String name, String image, String description);
    Page<BrowseHistory> getUserBrowseHistory(String userId, Pageable pageable);
    Page<BrowseHistory> getUserBrowseHistoryByType(String userId, TargetType targetType, Pageable pageable);
    void deleteBrowseHistory(String userId, TargetType targetType, String targetId);
} 