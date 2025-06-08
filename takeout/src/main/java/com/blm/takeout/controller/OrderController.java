package com.blm.takeout.controller;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Order.OrderStatus;
import com.blm.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getUserOrders(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByUserId(userId, pageRequest);
            
            List<Map<String, Object>> orderList = orders.getContent().stream()
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("state", order.getStatus().toString().toLowerCase());
                    orderMap.put("fullAddress", order.getDeliveryAddress());
                    
                    // 设置店铺信息
                    Map<String, Object> shopInfo = new HashMap<>();
                    shopInfo.put("id", order.getShopId());
                    shopInfo.put("name", order.getShopName());
                    shopInfo.put("image", order.getShopImage());
                    shopInfo.put("address", order.getShopAddress());
                    orderMap.put("shop", shopInfo);
                    
                    // 设置商品信息
                    List<Map<String, Object>> items = order.getOrderItems().stream()
                        .map(item -> {
                            Map<String, Object> itemMap = new HashMap<>();
                            Map<String, Object> productInfo = new HashMap<>();
                            productInfo.put("id", item.getItemId());
                            productInfo.put("name", item.getItemName());
                            productInfo.put("description", item.getItemDescription());
                            productInfo.put("price", item.getUnitPrice());
                            productInfo.put("image", item.getItemImage());
                            
                            itemMap.put("product", productInfo);
                            itemMap.put("quantity", item.getQuantity());
                            return itemMap;
                        })
                        .collect(Collectors.toList());
                    orderMap.put("items", items);
                    
                    return orderMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", orderList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取订单历史失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable String orderNumber) {
        try {
            OrderDTO order = orderService.getOrderDetail(orderNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status.toString());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "订单状态更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{orderId}/rider-location")
    public ResponseEntity<Map<String, Object>> updateRiderLocation(
            @PathVariable Integer orderId,
            @RequestParam String location) {
        try {
            orderService.updateRiderLocation(orderId, location);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "骑手位置更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 