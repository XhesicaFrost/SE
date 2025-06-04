package com.blm.takeout.service;

import com.blm.takeout.dto.BrowseHistoryDTO;
import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.BrowseHistoryRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BrowseHistoryService {
    private final BrowseHistoryRepository browseHistoryRepository;
    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BrowseHistoryService(BrowseHistoryRepository browseHistoryRepository,
                              ShopRepository shopRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.browseHistoryRepository = browseHistoryRepository;
        this.shopRepository = shopRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addBrowseHistory(Integer userId, BrowseHistory.TargetType targetType, Integer targetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        BrowseHistory history = new BrowseHistory();
        history.setUser(user);
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        history.setBrowseTime(LocalDateTime.now());
        browseHistoryRepository.save(history);
    }

    public Page<BrowseHistoryDTO> getUserBrowseHistory(Integer userId, Pageable pageable) {
        Page<BrowseHistory> histories = browseHistoryRepository.findByUserIdOrderByBrowseTimeDesc(userId, pageable);
        return histories.map(this::convertToDTO);
    }

    public Page<BrowseHistoryDTO> getUserBrowseHistoryByType(Integer userId, 
            BrowseHistory.TargetType targetType, Pageable pageable) {
        Page<BrowseHistory> histories = browseHistoryRepository
                .findByUserIdAndTargetTypeOrderByBrowseTimeDesc(userId, targetType, pageable);
        return histories.map(this::convertToDTO);
    }

    @Transactional
    public void deleteBrowseHistory(Integer userId, BrowseHistory.TargetType targetType, Integer targetId) {
        browseHistoryRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    private BrowseHistoryDTO convertToDTO(BrowseHistory history) {
        BrowseHistoryDTO dto = new BrowseHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUser().getUserid());
        dto.setTargetType(history.getTargetType());
        dto.setTargetId(history.getTargetId());
        dto.setBrowseTime(history.getBrowseTime());

        if (history.getTargetType() == BrowseHistory.TargetType.SHOP) {
            Shop shop = shopRepository.findById(history.getTargetId())
                    .orElse(null);
            if (shop != null) {
                dto.setName(shop.getName());
                dto.setImage(shop.getImage());
                dto.setDescription(shop.getDescription());
            }
        } else {
            Item item = itemRepository.findById(history.getTargetId())
                    .orElse(null);
            if (item != null) {
                dto.setName(item.getName());
                dto.setImage(item.getImage());
                dto.setDescription(item.getDescription());
            }
        }

        return dto;
    }
} 