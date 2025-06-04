package com.blm.takeout.service;

import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.ItemCategory;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ItemCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @Autowired
    public ShopService(
            ShopRepository shopRepository,
            ItemRepository itemRepository,
            ItemCategoryRepository itemCategoryRepository) {
        this.shopRepository = shopRepository;
        this.itemRepository = itemRepository;
        this.itemCategoryRepository = itemCategoryRepository;
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

    public Map<String, Object> getShopDetails(Integer shopId) {
        try {
            if (shopId == null) {
                throw new IllegalArgumentException("店铺ID不能为空");
            }

            Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在，ID: " + shopId));

            if (shop == null) {
                throw new RuntimeException("店铺数据为空，ID: " + shopId);
            }

            // 验证必需字段
            if (shop.getName() == null) throw new RuntimeException("店铺名称不能为空");
            if (shop.getType() == null) throw new RuntimeException("店铺类型不能为空");
            if (shop.getRating() == null) throw new RuntimeException("店铺评分不能为空");
            if (shop.getSales() == null) throw new RuntimeException("店铺销量不能为空");
            if (shop.getMinPrice() == null) throw new RuntimeException("店铺最低价格不能为空");
            if (shop.getMaxPrice() == null) throw new RuntimeException("店铺最高价格不能为空");
            if (shop.getDeliveryFee() == null) throw new RuntimeException("店铺配送费不能为空");
            if (shop.getDeliveryTime() == null) throw new RuntimeException("店铺配送时间不能为空");
            if (shop.getAddress() == null) throw new RuntimeException("店铺地址不能为空");
            if (shop.getLatitude() == null) throw new RuntimeException("店铺纬度不能为空");
            if (shop.getLongitude() == null) throw new RuntimeException("店铺经度不能为空");
            if (shop.getPhone() == null) throw new RuntimeException("店铺电话不能为空");
            if (shop.getBusinessHours() == null) throw new RuntimeException("店铺营业时间不能为空");
            if (shop.getIsOpen() == null) throw new RuntimeException("店铺营业状态不能为空");

            List<Item> hotItems = itemRepository.findTop5ByShopIdOrderBySalesDesc(shopId);
            List<ItemCategory> categories = itemCategoryRepository.findByShopIdOrderBySortOrderAsc(shopId);

            Map<String, Object> shopDetails = new HashMap<>();
            shopDetails.put("id", shop.getId());
            shopDetails.put("name", shop.getName());
            shopDetails.put("type", shop.getType());
            shopDetails.put("rating", shop.getRating());
            shopDetails.put("minPrice", shop.getMinPrice());
            shopDetails.put("maxPrice", shop.getMaxPrice());
            shopDetails.put("deliveryTime", shop.getDeliveryTime());
            shopDetails.put("location", Map.of(
                "latitude", shop.getLatitude(),
                "longitude", shop.getLongitude()
            ));
            shopDetails.put("address", shop.getAddress());
            shopDetails.put("businessHours", shop.getBusinessHours());
            shopDetails.put("phone", shop.getPhone());
            shopDetails.put("description", shop.getDescription());
            shopDetails.put("isOpen", shop.getIsOpen());

            // 添加热销商品
            shopDetails.put("hotItems", hotItems.stream()
                .map(item -> Map.of(
                    "id", item.getId(),
                    "name", item.getName(),
                    "price", item.getPrice(),
                    "sales", item.getSales(),
                    "image", item.getImage()
                ))
                .collect(Collectors.toList()));

            // 添加商品分类
            shopDetails.put("categories", categories.stream()
                .map(category -> {
                    List<Item> items = itemRepository.findByShopIdAndCategoryId(shopId, category.getId());
                    return Map.of(
                        "id", category.getId(),
                        "name", category.getName(),
                        "items", items.stream()
                            .map(item -> Map.of(
                                "id", item.getId(),
                                "name", item.getName(),
                                "price", item.getPrice(),
                                "image", item.getImage(),
                                "description", item.getDescription(),
                                "sales", item.getSales()
                            ))
                            .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList()));

            return shopDetails;
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误堆栈
            throw new RuntimeException("获取店铺详情失败: " + e.getMessage() + "\n" + e.getStackTrace()[0], e);
        }
    }

    public List<Map<String, Object>> getShopHotItems(Integer shopId, Integer limit) {
        List<Item> items = itemRepository.findTop5ByShopIdOrderBySalesDesc(shopId);
        if (limit != null && limit > 0) {
            items = items.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }

        return items.stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("price", item.getPrice());
                itemMap.put("image", item.getImage());
                itemMap.put("sales", item.getSales());
                itemMap.put("rating", item.getRating());
                return itemMap;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getShopCategories(Integer shopId) {
        List<ItemCategory> categories = itemCategoryRepository.findByShopIdOrderBySortOrderAsc(shopId);
        
        return categories.stream()
            .map(category -> {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", category.getId());
                categoryMap.put("name", category.getName());
                
                List<Item> items = itemRepository.findByShopIdAndCategoryId(shopId, category.getId());
                categoryMap.put("items", items.stream()
                    .map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("id", item.getId());
                        itemMap.put("name", item.getName());
                        itemMap.put("price", item.getPrice());
                        itemMap.put("image", item.getImage());
                        itemMap.put("description", item.getDescription());
                        itemMap.put("sales", item.getSales());
                        return itemMap;
                    })
                    .collect(Collectors.toList()));
                
                return categoryMap;
            })
            .collect(Collectors.toList());
    }
} 