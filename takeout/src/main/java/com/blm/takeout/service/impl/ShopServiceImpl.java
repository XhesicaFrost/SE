package com.blm.takeout.service.impl;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.dto.TagDTO;
import com.blm.takeout.dto.ProductImageDTO;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.ItemCategory;
import com.blm.takeout.entity.Tag;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ItemCategoryRepository;
import com.blm.takeout.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @Autowired
    public ShopServiceImpl(
            ShopRepository shopRepository,
            ItemRepository itemRepository,
            ItemCategoryRepository itemCategoryRepository) {
        this.shopRepository = shopRepository;
        this.itemRepository = itemRepository;
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @Override
    public List<ShopDTO> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ShopDTO getShopById(Integer shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("商店不存在"));
        return convertToDTO(shop);
    }

    private ShopDTO convertToDTO(Shop shop) {
        ShopDTO dto = new ShopDTO();
        dto.setId(shop.getId());
        dto.setImage(shop.getImage());
        dto.setName(shop.getName());
        dto.setRating(shop.getRating());
        dto.setAvgPrice(shop.getAvgPrice());
        dto.setDistance(shop.getDistance());
        dto.setDeliverTime(shop.getDeliverTime());
        dto.setAddress(shop.getAddress());
        dto.setSales(shop.getSales());
        
        // 设置标签
        dto.setTags(shop.getTags().stream()
                .map(tag -> {
                    TagDTO tagDTO = new TagDTO();
                    tagDTO.setTag(tag.getTag());
                    return tagDTO;
                })
                .collect(Collectors.toList()));
        
        // 设置商品图片（只取前三个）
        dto.setProducts(shop.getItems().stream()
                .limit(3)
                .map(item -> {
                    ProductImageDTO productDTO = new ProductImageDTO();
                    productDTO.setImage(item.getImage());
                    return productDTO;
                })
                .collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    public Page<Shop> searchShops(String keyword, Pageable pageable) {
        return shopRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Map<String, Object> getShopDetails(Integer shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("name", shop.getName());
        result.put("image", shop.getImage());
        result.put("address", shop.getAddress());
        result.put("rating", shop.getRating());
        result.put("monthlySales", shop.getSales());
        result.put("deliveryTime", shop.getDeliverTime());
        return result;
    }

    @Override
    public Map<String, Object> getShopHotItems(Integer shopId, int limit) {
        List<Item> items = itemRepository.findTop5ByShopIdOrderBySalesDesc(shopId);
        if (limit > 0) {
            items = items.stream().limit(limit).collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("image", item.getImage());
                    itemMap.put("sales", item.getSales());
                    return itemMap;
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Map<String, Object> getShopCategories(Integer shopId) {
        List<ItemCategory> categories = itemCategoryRepository.findByShopIdOrderBySortOrderAsc(shopId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("id", category.getId());
                    categoryMap.put("name", category.getName());
                    return categoryMap;
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<ShopDTO> getRecommendedShops(Integer userId) {
        // 获取用户历史订单中的商店
        List<Shop> userShops = shopRepository.findShopsByUserOrders(userId);
        
        // 如果用户没有历史订单，返回评分最高的商店
        if (userShops.isEmpty()) {
            userShops = shopRepository.findTop10ByOrderByRatingDesc();
        }
        
        return userShops.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 