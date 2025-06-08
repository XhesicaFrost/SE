package com.blm.takeout.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.SellerService;
import com.blm.takeout.util.FileUtils;
import com.blm.takeout.entity.Seller;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {
    private final SellerService sellerService;
    @PostMapping("/register")
    public ApiResponse<?> registerSeller(@RequestParam String shopName,
                                         @RequestParam String shopAddress,
                                         @RequestParam MultipartFile shopImage,
                                         @RequestParam Integer userId) {
        try {
            sellerService.registerSeller(shopName, shopAddress, shopImage, userId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ApiResponse<?> editShopInfo(@RequestParam String shopName,
                                    @RequestParam String shopAddress,
                                    @RequestParam(required = false) MultipartFile shopImage,
                                    @RequestParam Integer sellerId) {
        try {
            sellerService.editSellerInfo(sellerId, shopName, shopAddress, shopImage);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/shop")
    public ApiResponse<?> getShopInfo(@RequestParam Integer sellerId) {
        try {
            Seller seller = sellerService.getSellerById(sellerId);
            if (seller != null) {
                String base64Image = FileUtils.convertImageToBase64(seller.getImage());
                return ApiResponse.success(Map.of(
                    "shopname", seller.getName(),
                    "shopAddress", seller.getAddress(),
                    "shopImg", base64Image
                ));
            } else {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商家信息不存在");
            } 
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INSUFFICIENT_STORAGE.value(), e.getMessage());
        }
    }
}
