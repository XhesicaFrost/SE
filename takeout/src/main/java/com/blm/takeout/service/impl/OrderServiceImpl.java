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
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
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
    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(Order.OrderStatus.valueOf(status));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateRiderLocation(Integer orderId, String location) {
        Order order = getOrderById(orderId);
        order.setRiderLocation(location);
        return orderRepository.save(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setShopId(order.getShopId());
        dto.setShopName(order.getShopName());
        dto.setShopImage(order.getShopImage());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryPhone(order.getDeliveryPhone());
        dto.setDeliveryName(order.getDeliveryName());
        dto.setRiderId(order.getRiderId());
        dto.setRiderName(order.getRiderName());
        dto.setRiderPhone(order.getRiderPhone());
        dto.setRiderLocation(order.getRiderLocation());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setItemDescription(item.getItemDescription());
        dto.setItemImage(item.getItemImage());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getUnitPrice() * item.getQuantity());
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

            Shop shop = shopRepository.findById(order.getShopId())
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
                        productMap.put("id", item.getItemId());
                        productMap.put("name", item.getItemName());
                        productMap.put("description", item.getItemDescription());
                        productMap.put("price", item.getUnitPrice());
                        productMap.put("image", item.getItemImage());
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

    @Override
    public void updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
    }
} 