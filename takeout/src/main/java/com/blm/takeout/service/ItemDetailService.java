package com.blm.takeout.service;

import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.ItemCategory;
import com.blm.takeout.entity.ItemReview;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.ItemCategoryRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ItemReviewRepository;
import com.blm.takeout.repository.ShopRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemDetailService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ItemCategoryRepository categoryRepository;

    @Autowired
    private ItemReviewRepository reviewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> getItemDetail(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        Shop shop = shopRepository.findById(item.getShopId())
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        ItemCategory category = categoryRepository.findById(item.getCategoryId())
                .orElseThrow(() -> new RuntimeException("商品分类不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("name", item.getName());
        result.put("description", item.getDescription());
        result.put("price", item.getPrice());
        result.put("image", item.getImage());
        result.put("sales", item.getSales());
        result.put("rating", item.getRating());
        result.put("status", item.getStatus());
        result.put("category", convertCategoryToMap(category));
        result.put("shop", convertShopToMap(shop));

        return result;
    }

    public Map<String, Object> getItemReviews(Integer itemId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ItemReview> reviewPage = reviewRepository.findByItemIdOrderByCreatedAtDesc(itemId, pageable);

        List<Map<String, Object>> reviews = reviewPage.getContent().stream()
                .map(this::convertReviewToMap)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        result.put("total", reviewPage.getTotalElements());
        result.put("pages", reviewPage.getTotalPages());
        result.put("current", page);

        return result;
    }

    private Map<String, Object> convertCategoryToMap(ItemCategory category) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        return map;
    }

    private Map<String, Object> convertShopToMap(Shop shop) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", shop.getId());
        map.put("name", shop.getName());
        map.put("image", shop.getImage());
        map.put("rating", shop.getRating());
        return map;
    }

    private Map<String, Object> convertReviewToMap(ItemReview review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("userId", review.getUserId());
        map.put("rating", review.getRating());
        map.put("content", review.getContent());
        try {
            if (review.getImages() != null && !review.getImages().isEmpty()) {
                List<String> images = objectMapper.readValue(review.getImages(), new TypeReference<List<String>>() {});
                map.put("images", images);
            } else {
                map.put("images", List.of());
            }
        } catch (JsonProcessingException e) {
            map.put("images", List.of());
        }
        map.put("createdAt", review.getCreatedAt());
        return map;
    }
} 