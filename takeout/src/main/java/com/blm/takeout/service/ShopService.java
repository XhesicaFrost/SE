package com.blm.takeout.service;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ShopService {
    List<ShopDTO> getAllShops();
    ShopDTO getShopById(Integer shopId);
    Page<Shop> searchShops(String keyword, Pageable pageable);
    Map<String, Object> getShopDetails(Integer shopId);
    Map<String, Object> getShopHotItems(Integer shopId, int limit);
    List<ShopDTO> getRecommendedShops(Integer userId);
    Shop getShopByUserId(Integer userId);
    void updateShopImage(Integer shopId, MultipartFile image) throws Exception;
    List<Shop> getShopsByUserOrders(Integer userId);
    void updateShop(Shop shop);
} 