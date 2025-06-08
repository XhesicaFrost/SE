package com.blm.takeout.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blm.takeout.dto.RiderDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Rider;
import com.blm.takeout.entity.Rider.RiderStatus;

public interface RiderService {
    // 基础信息操作
    RiderDTO register(Rider rider);
    RiderDTO login(String phone, String password);
    RiderDTO updateStatus(Integer riderId, RiderStatus status);
    RiderDTO updateLocation(Integer riderId, Double latitude, Double longitude, String location);
    RiderDTO updateProfile(Integer riderId, RiderDTO riderDTO);
    RiderDTO getRiderById(Integer riderId);
    RiderDTO getRiderByPhone(String phone);
    
    // 订单相关操作
    List<Order> getAcceptedOrders(Integer riderId);
    List<Order> getRecommendedOrders(Integer riderId, Double latitude, Double longitude);
    Order acceptOrder(Integer riderId, Integer orderId);
    Order pickupOrder(Integer riderId, Integer orderId);
    Order completeOrder(Integer riderId, Integer orderId);
    
    // 订单查询
    List<Order> getFilteredOrders(Integer riderId, String merchantName, String userAddress);
    Order getOrderDetail(Integer riderId, Integer orderId);
    Page<Order> getRiderOrders(Integer riderId, Pageable pageable);
    
    // 智能派单相关
    void processSmartDispatch(Order order);
    List<Rider> findNearbyRiders(Double latitude, Double longitude, Double maxDistance);
    
    // 统计相关
    Map<String, Object> getRiderStatistics(Integer riderId);
} 