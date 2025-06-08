package com.blm.takeout.service;

import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop getShopById(Integer id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    public Page<Shop> searchShops(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return shopRepository.findAll(pageable);
        }
        return shopRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
    }
} 