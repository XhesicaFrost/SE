package com.blm.takeout.service.impl;

import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.BrowseHistory.TargetType;
import com.blm.takeout.repository.BrowseHistoryRepository;
import com.blm.takeout.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    @Autowired
    private BrowseHistoryRepository browseHistoryRepository;

    @Override
    @Transactional
    public BrowseHistory addBrowseHistory(String userId, TargetType targetType, String targetId, String name, String image, String description) {
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        history.setName(name);
        history.setImage(image);
        history.setDescription(description);
        return browseHistoryRepository.save(history);
    }

    @Override
    public Page<BrowseHistory> getUserBrowseHistory(String userId, Pageable pageable) {
        return browseHistoryRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<BrowseHistory> getUserBrowseHistoryByType(String userId, TargetType targetType, Pageable pageable) {
        return browseHistoryRepository.findByUserIdAndTargetType(userId, targetType, pageable);
    }

    @Override
    @Transactional
    public void deleteBrowseHistory(String userId, TargetType targetType, String targetId) {
        browseHistoryRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }
} 