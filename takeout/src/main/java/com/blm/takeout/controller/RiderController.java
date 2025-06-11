package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.OrderService;
import com.blm.takeout.service.RiderService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/updateLocation")
    public ApiResponse<?> updateLocation(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Double latitude = (Double) request.get("latitude");
            Double longitude = (Double) request.get("longitude");
            riderService.updateLocaction(userId, latitude, longitude);
            return ApiResponse.success(Map.of("success", "true"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/acceptedorders")
    public ApiResponse<?> getAcceptedOrders(@RequestParam Integer riderId) {
        try {
            List<Map<String, Object>> orders = riderService.getAcceptedOrders(riderId);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取已接订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommendedorders")
    public ApiResponse<?> getRecommendedOrders(
            @RequestParam Integer riderId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            List<Map<String, Object>> orders = riderService.getRecommendedOrders(riderId, latitude, longitude);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取推荐订单失败: " + e.getMessage());
        }
    }

    @PostMapping("/updateorder")
    public ApiResponse<?> updateOrderStatus(@RequestBody Map<String, Object> request) {
        try {
            Integer riderId = Integer.parseInt(request.get("riderId").toString());
            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            String status = request.get("status").toString();
            boolean success = riderService.updateOrderStatus(riderId, orderId, status);
            if (success) {
                return ApiResponse.success(Map.of("success", true));
            } else {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "状态更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "状态更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/chooseorder")
    public ApiResponse<?> chooseOrder(@RequestBody Map<String, Object> request) {
        try {
            Integer riderId = Integer.parseInt(request.get("riderId").toString());
            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            boolean success = riderService.chooseOrder(riderId, orderId);
            if (success) {
                return ApiResponse.success(Map.of("success", true));
            } else {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "该订单可能已被其他骑手接取");
            }
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "抢单失败: " + e.getMessage());
        }
    }
}
