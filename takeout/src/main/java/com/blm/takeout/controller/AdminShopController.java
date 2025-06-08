package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Seller;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.SellerRepository;
import com.blm.takeout.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminShopController {
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final SellerRepository sellerRepository;

    @GetMapping("/shops")
    public ApiResponse<?> getAllShops() {
        try {
            List<ShopDTO> shops = shopService.getAllShops();
            List<Map<String, Object>> shopList = new ArrayList<>();
            
            for (ShopDTO shop : shops) {
                Map<String, Object> shopMap = new HashMap<>();
                shopMap.put("id", shop.getId());
                shopMap.put("name", shop.getName());
                shopMap.put("address", shop.getAddress());
                shopMap.put("status", shop.getStatus());
                
                // 将图片转换为base64
                if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                    try {
                        String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                        shopMap.put("image", base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        shopMap.put("image", "");
                    }
                } else {
                    shopMap.put("image", "");
                }
                
                shopList.add(shopMap);
            }
            
            return ApiResponse.success(shopList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/shop/{id}")
    public ApiResponse<?> getShopById(@PathVariable Integer id) {
        try {
            ShopDTO shop = shopService.getShopById(id);
            if (shop == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "店铺不存在");
            }
            Map<String, Object> shopMap = new HashMap<>();
            shopMap.put("id", shop.getId());
            shopMap.put("name", shop.getName());
            shopMap.put("address", shop.getAddress());
            shopMap.put("status", shop.getStatus());
            // 将图片转换为base64
            if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                    shopMap.put("image", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    shopMap.put("image", "");
                }
            } else {
                shopMap.put("image", "");
            }
            return ApiResponse.success(shopMap);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/shop/status")
    public ApiResponse<?> updateShopStatus(@RequestBody Map<String, Object> params) {
        try {
            Integer shopId = (Integer) params.get("shopId");
            String status = (String) params.get("status");
            
            if (shopId == null || status == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "缺少必要参数");
            }

            Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

            Seller seller = sellerRepository.findByUser_Userid(shop.getUserId())
                .orElseThrow(() -> new RuntimeException("商家不存在"));

            if ("enable".equals(status)) {
                shop.setStatus(Shop.Status.正常);
                seller.setSellerStatus(Seller.Status.正常);
            } else if ("disable".equals(status)) {
                shop.setStatus(Shop.Status.封禁中);
                seller.setSellerStatus(Seller.Status.封禁中);
            } else {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "无效的状态值");
            }

            shopService.updateShop(shop);
            sellerRepository.save(seller);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/shop/edit")
    public ApiResponse<?> editShop(
            @RequestParam Integer shopId,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Shop shop = shopService.getShopByUserId(shopId);
            if (shop == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "店铺不存在");
            }

            // 更新店铺信息
            shop.setName(name);
            shop.setAddress(address);
            
            if (image != null && !image.isEmpty()) {
                shopService.updateShopImage(shopId, image);
            }

            // 保存更新
            shopService.updateShop(shop);
            
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
} 