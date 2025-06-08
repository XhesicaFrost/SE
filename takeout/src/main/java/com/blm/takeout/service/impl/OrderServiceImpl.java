package com.blm.takeout.service.impl;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.dto.OrderItemDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.OrderItem;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ShopRepository shopRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Page<Order> orders = orderRepository.findByUser_UseridOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    public OrderDTO getOrderDetail(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateRiderLocation(Integer orderId, String location) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setRiderLocation(location);
        orderRepository.save(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getUserid());
        dto.setShopId(order.getShop().getId());
        dto.setShopName(order.getShop().getName());
        dto.setShopImage(order.getShop().getImage());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryLatitude(order.getDeliveryLatitude());
        dto.setDeliveryLongitude(order.getDeliveryLongitude());
        dto.setDeliveryPhone(order.getDeliveryPhone());
        dto.setDeliveryName(order.getDeliveryName());
        dto.setRiderId(order.getRiderId());
        dto.setRiderName(order.getRiderName());
        dto.setRiderPhone(order.getRiderPhone());
        dto.setRiderLatitude(order.getRiderLatitude());
        dto.setRiderLongitude(order.getRiderLongitude());
        dto.setRiderLocation(order.getRiderLocation());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        dto.setPickupTime(order.getPickupTime());
        dto.setCompletedTime(order.getCompletedTime());
        dto.setRiderArrivedShopTime(order.getRiderArrivedShopTime());
        dto.setIsSmartAssignment(order.getIsSmartAssignment());
        dto.setUrgentCount(order.getUrgentCount());
        dto.setIsUrgent(order.getIsUrgent());
        dto.setPlannedRoute(order.getPlannedRoute());

        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(itemDTOs);

        return dto;
    }

    private OrderItemDTO convertToItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setItemId(item.getItem().getId());
        dto.setItemName(item.getItem().getName());
        dto.setItemImage(item.getItem().getImage());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    @Override
    public OrderDTO getOrderDetail(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }

    @Override
    public Page<Order> getOrdersByUserId(Integer userId, PageRequest pageRequest) {
        return orderRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Map<String, Object>> getOrderHistory(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("state", order.getStatus());
            orderMap.put("fullAddress", order.getDeliveryAddress());

            Shop shop = shopRepository.findById(order.getShop().getId())
                    .orElseThrow(() -> new RuntimeException("店铺不存在"));
            
            Map<String, Object> shopMap = new HashMap<>();
            shopMap.put("id", shop.getId());
            shopMap.put("name", shop.getName());
            shopMap.put("image", shop.getImage());
            shopMap.put("address", shop.getAddress());
            orderMap.put("shop", shopMap);

            List<Map<String, Object>> items = order.getOrderItems().stream()
                    .map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("id", item.getItem().getId());
                        productMap.put("name", item.getItem().getName());
                        productMap.put("description", item.getItem().getDescription());
                        productMap.put("price", item.getUnitPrice());
                        productMap.put("image", item.getItem().getImage());
                        itemMap.put("product", productMap);
                        itemMap.put("quantity", item.getQuantity());
                        return itemMap;
                    })
                    .collect(Collectors.toList());
            orderMap.put("items", items);

            return orderMap;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByShopId(Integer shopId) {
        return orderRepository.findByShopId(shopId);
    }
} 