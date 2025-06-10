package com.blm.takeout.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.SellerService;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.util.FileUtils;
import com.blm.takeout.entity.Seller;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Order;
import com.blm.takeout.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {
    private final SellerService sellerService;
    private final ShopService shopService;
    private final ShopRepository shopRepository;

    @PostMapping("/register")
    public ApiResponse<?> registerSeller(@RequestParam String shopName,
                                         @RequestParam String shopAddress,
                                         @RequestParam String shopTags,
                                         @RequestParam MultipartFile shopImage,
                                         @RequestParam Integer userId) {
        try {
            sellerService.registerSeller(shopName, shopAddress, shopTags, shopImage, userId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ApiResponse<?> editSeller(@RequestParam Integer sellerId,
                                    @RequestParam String name,
                                    @RequestParam String address,
                                    @RequestParam(required = false) MultipartFile image,
                                    @RequestParam String shopTags) {
        try {
            Seller seller = sellerService.getSellerById(sellerId);
            if (seller == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商家不存在");
            }

            // 更新商家信息
            seller.setName(name);
            seller.setAddress(address);
            if (image != null && !image.isEmpty()) {
                String imagePath = FileUtils.saveImage(image);
                seller.setImage(imagePath);
            }
            seller.setTags(sellerService.parseTags(shopTags));
            seller.setSellerStatus(Seller.Status.审批中);
            sellerService.saveSeller(seller);

            // 更新店铺信息
            Shop shop = shopRepository.findByUserId(seller.getUser().getUserid());
            if (shop != null) {
                shop.setName(name);
                shop.setAddress(address);
                if (image != null && !image.isEmpty()) {
                    shop.setImage(seller.getImage());
                }
                shop.setStatus(Shop.Status.审批中);
                shopService.updateShop(shop);
            }

            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/shop")
    public ApiResponse<?> getShopInfo(@RequestParam Integer sellerId) {
        try {
            Seller seller = sellerService.getSellerById(sellerId);
            if (seller != null) {
                String base64Image = FileUtils.convertImageToBase64(seller.getImage());
                return ApiResponse.success(Map.of(
                    "shopName", seller.getName(),
                    "shopAddress", seller.getAddress(),
                    "shopImg", base64Image,
                    "shopTags", seller.getTags()
                ));
            } else {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商家信息不存在");
            } 
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage());
        }
    }

    @GetMapping("/order")
    public ApiResponse<?> getOrders(@RequestParam Integer sellerId) {
        try {
            List<Order> orders = sellerService.getOrders(sellerId);
            List<Map<String, Object>> response = orders.stream().map(order -> Map.of(
                "id", order.getId(),
                "totalPrice", order.getTotalAmount(),
                "status", order.getStatus(),
                "items", order.getOrderItems().stream().map(item -> Map.of(
                    "name", item.getItem().getName(),
                    "count", item.getQuantity(),
                    "price", item.getUnitPrice()
                )).collect(Collectors.toList())
            )).collect(Collectors.toList());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/order/serve")
    public ApiResponse<?> serveOrder(@RequestBody Map<String, String> request) {
        try {
            String orderIdStr = request.get("orderId");
            if (orderIdStr == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "订单ID不能为空");
            }

            Integer orderId = Integer.parseInt(orderIdStr);
            sellerService.serveOrder(orderId);

            return ApiResponse.success(Map.of("success", true));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败: " + e.getMessage());
        }
    }
}
