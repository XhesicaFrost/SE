package com.blm.takeout.service.impl;

import com.blm.takeout.dto.ItemDTO;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.ItemReview;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.ItemReviewRepository;
import com.blm.takeout.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final ItemReviewRepository itemReviewRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository,
            ShopRepository shopRepository,
            ItemReviewRepository itemReviewRepository) {
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
        this.itemReviewRepository = itemReviewRepository;
    }

    @Override
    public Item getItemById(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public Page<Item> searchItems(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
    }

    @Override
    public Page<Item> getItemsByShopId(Integer shopId, Pageable pageable) {
        return itemRepository.findByShopId(shopId, pageable);
    }

    @Override
    public Map<String, Object> getItemDetails(Integer itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        Shop shop = shopRepository.findById(item.getShopId())
            .orElseThrow(() -> new RuntimeException("店铺不存在"));

        List<ItemReview> reviews = itemReviewRepository.findByItemIdOrderByCreatedAtDesc(itemId);

        Map<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("id", item.getId());
        itemDetails.put("name", item.getName());
        itemDetails.put("price", item.getPrice());
        itemDetails.put("image", item.getImage());
        itemDetails.put("description", item.getDescription());
        itemDetails.put("sales", item.getSales());
        itemDetails.put("rating", item.getRating());
        itemDetails.put("shopId", item.getShopId());
        itemDetails.put("shopName", shop.getName());
        itemDetails.put("category", item.getCategoryId());

        itemDetails.put("reviews", reviews.stream()
            .map(review -> Map.of(
                "id", review.getId(),
                "userId", review.getUserId(),
                "username", "用户" + review.getUserId(),
                "rating", review.getRating(),
                "content", review.getContent(),
                "createTime", review.getCreatedAt(),
                "images", review.getImages()
            ))
            .collect(Collectors.toList()));

        return itemDetails;
    }

    @Override
    public List<ItemDTO> getItemsByShopId(Integer shopId) {
        List<Item> items = itemRepository.findByShopId(shopId);
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setImage(item.getImage());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        return dto;
    }
} 