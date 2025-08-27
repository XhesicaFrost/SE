package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Seller;
import com.blm.takeout.service.SellerService;

import lombok.RequiredArgsConstructor;

import java.util.Map;
@RestController
@RequiredArgsConstructor
public class UserToSellerController {
    private final SellerService sellerService;

    @GetMapping("/userToseller")
    public ApiResponse<?> userToSeller(@RequestParam Integer userId) {
        try {
            Seller seller = sellerService.getSellerByUserId(userId);
            if (seller != null) {
                return ApiResponse.success(Map.of(
                    "sellerId", seller.getId(),
                    "sellerName", seller.getName(),
                    "sellerStatus", seller.getSellerStatus().name()
                ));
            } else {
                return ApiResponse.success(Map.of(
                    "sellerId", "无",
                    "sellerName", "无",
                    "sellerStatus", Seller.Status.未注册.name()
                ));
            }
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
