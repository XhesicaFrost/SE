package com.blm.takeout.controller;

import com.blm.takeout.dto.RiderDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Rider;
import com.blm.takeout.entity.Rider.RiderStatus;
import com.blm.takeout.service.RiderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rider")
public class RiderController {
    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @GetMapping("/acceptedorders")
    public ResponseEntity<Map<String, Object>> getAcceptedOrders(@RequestParam String riderId) {
        try {
            List<Order> orders = riderService.getAcceptedOrders(riderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders.stream().map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("sellerName", order.getShop().getName());
                orderMap.put("sellerAddress", order.getShop().getAddress());
                orderMap.put("userAddress", order.getDeliveryAddress());
                orderMap.put("userPhone", order.getDeliveryPhone());
                orderMap.put("status", order.getStatus().toString().toLowerCase());
                orderMap.put("createTime", order.getCreatedAt().toString().replace('T', ' ').substring(0, 16));
                return orderMap;
            }).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/recommendedorders")
    public ResponseEntity<Map<String, Object>> getRecommendedOrders(
            @RequestParam String riderId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            List<Order> orders = riderService.getRecommendedOrders(riderId, latitude, longitude);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders.stream().map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("sellerName", order.getShop().getName());
                orderMap.put("sellerAddress", order.getShop().getAddress());
                orderMap.put("userAddress", order.getDeliveryAddress());
                orderMap.put("createTime", order.getCreatedAt().toString().replace('T', ' ').substring(0, 16));
                return orderMap;
            }).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/chooseorder")
    public ResponseEntity<Map<String, Object>> chooseOrder(@RequestBody Map<String, Object> request) {
        try {
            String riderId = request.get("riderId").toString();
            String orderId = request.get("orderId").toString();
            riderService.acceptOrder(Integer.parseInt(riderId), Integer.parseInt(orderId));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/updateorder")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @RequestBody Map<String, Object> request) {
        try {
            String riderId = request.get("riderId").toString();
            String orderId = request.get("orderId").toString();
            String status = request.get("status").toString();
            
            Order order;
            if ("picked".equals(status)) {
                order = riderService.pickupOrder(Integer.parseInt(riderId), Integer.parseInt(orderId));
            } else if ("completed".equals(status)) {
                order = riderService.completeOrder(Integer.parseInt(riderId), Integer.parseInt(orderId));
            } else {
                throw new IllegalArgumentException("Invalid status");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "状态更新失败");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/orderfiltered")
    public ResponseEntity<Map<String, Object>> getFilteredOrders(
            @RequestParam String userId,
            @RequestParam(required = false) String sellerName,
            @RequestParam(required = false) String userAddress) {
        try {
            List<Order> orders = riderService.getFilteredOrders(userId, sellerName, userAddress);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders.stream().map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("sellerName", order.getShop().getName());
                orderMap.put("sellerAddress", order.getShop().getAddress());
                orderMap.put("userAddress", order.getDeliveryAddress());
                orderMap.put("createTime", order.getCreatedAt().toString().replace('T', ' ').substring(0, 16));
                return orderMap;
            }).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/updateLocation")
    public ResponseEntity<Map<String, Object>> updateLocation(@RequestBody Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            Double latitude = Double.parseDouble(request.get("latitude").toString());
            Double longitude = Double.parseDouble(request.get("longitude").toString());
            
            riderService.updateLocation(Integer.parseInt(userId), latitude, longitude, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/orderdetail")
    public ResponseEntity<Map<String, Object>> getOrderDetail(
            @RequestParam String riderId,
            @RequestParam String orderId) {
        try {
            Order order = riderService.getOrderDetail(Integer.parseInt(riderId), Integer.parseInt(orderId));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> data = new HashMap<>();
            data.put("id", order.getId());
            data.put("sellerName", order.getShop().getName());
            data.put("sellerAddress", order.getShop().getAddress());
            data.put("sellerLng", order.getShop().getLongitude());
            data.put("sellerLat", order.getShop().getLatitude());
            data.put("userAddress", order.getDeliveryAddress());
            data.put("userPhone", order.getDeliveryPhone());
            data.put("status", order.getStatus().toString().toLowerCase());
            data.put("createTime", order.getCreatedAt().toString().replace('T', ' ').substring(0, 16));
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistoryOrders(
            @RequestParam String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<Order> orders = riderService.getRiderOrders(
                Integer.parseInt(userId), 
                PageRequest.of(page - 1, pageSize)
            );
            
            Map<String, Object> data = new HashMap<>();
            data.put("orders", orders.getContent().stream().map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("sellerName", order.getShop().getName());
                orderMap.put("sellerAddress", order.getShop().getAddress());
                orderMap.put("userAddress", order.getDeliveryAddress());
                orderMap.put("userPhone", order.getDeliveryPhone());
                orderMap.put("createTime", order.getCreatedAt().toString().replace('T', ' ').substring(0, 16));
                orderMap.put("completeTime", order.getCompletedTime().toString().replace('T', ' ').substring(0, 16));
                return orderMap;
            }).toList());
            data.put("total", orders.getTotalElements());
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取历史订单失败");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/checkstatus")
    public ResponseEntity<Map<String, Object>> checkStatus(
            @RequestParam Integer riderId,
            @RequestParam(required = false) Integer orderId) {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取骑手信息
            RiderDTO riderDTO = riderService.getRiderById(riderId);
            
            data.put("rider", Map.of(
                "id", riderDTO.getId(),
                "name", riderDTO.getName(),
                "status", riderDTO.getStatus(),
                "activeOrders", riderDTO.getActiveOrderCount(),
                "latitude", riderDTO.getCurrentLatitude(),
                "longitude", riderDTO.getCurrentLongitude()
            ));
            
            // 如果提供了订单ID，也获取订单信息
            if (orderId != null) {
                Order order = riderService.getOrderDetail(riderId, orderId);
                
                data.put("order", Map.of(
                    "id", order.getId(),
                    "status", order.getStatus(),
                    "riderId", order.getRiderId()
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @RequestParam Integer riderId,
            @RequestParam String status) {
        try {
            RiderDTO rider = riderService.updateStatus(riderId, RiderStatus.valueOf(status));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rider);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 