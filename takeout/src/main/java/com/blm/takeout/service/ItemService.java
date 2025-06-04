package com.blm.takeout.service;

import com.blm.takeout.entity.Item;
import com.blm.takeout.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item getItemById(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Page<Item> searchItems(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
    }

    public Page<Item> getItemsByShopId(Integer shopId, Pageable pageable) {
        return itemRepository.findByShopId(shopId, pageable);
    }
} 