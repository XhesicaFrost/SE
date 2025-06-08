package com.blm.takeout.service;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;

public interface ShopService {
    List<ShopDTO> getAllShops();
    ShopDTO getShopById(Integer shopId);
    List<ShopDTO> getRecommendedShops(Integer userId);
    Page<Shop> searchShops(String keyword, Pageable pageable);
    Map<String, Object> getShopDetails(Integer shopId);
    Map<String, Object> getShopHotItems(Integer shopId, int limit);
    Map<String, Object> getShopCategories(Integer shopId);
} 