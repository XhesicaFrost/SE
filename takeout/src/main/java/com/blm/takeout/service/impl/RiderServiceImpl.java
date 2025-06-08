package com.blm.takeout.service.impl;

import com.blm.takeout.dto.RiderDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Order.OrderStatus;
import com.blm.takeout.entity.Rider;
import com.blm.takeout.entity.Rider.RiderStatus;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.repository.RiderRepository;
import com.blm.takeout.service.RiderService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Arrays;

@Service
public class RiderServiceImpl implements RiderService {
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    // 配置智能派单的各项权重
    private static final double DISTANCE_WEIGHT = 0.4;    // 距离权重
    private static final double RATING_WEIGHT = 0.2;      // 评分权重
    private static final double EXPERIENCE_WEIGHT = 0.2;  // 经验权重
    private static final double LOAD_WEIGHT = 0.2;        // 负载权重
    
    private static final int MAX_CONCURRENT_ORDERS = 3;   // 最大同时配送订单数
    private static final int EXPERIENCE_BENCHMARK = 1000; // 经验值基准（完成1000单为满分）
    private static final double MAX_REASONABLE_DISTANCE = 5.0; // 最大合理配送距离（公里）

    public RiderServiceImpl(RiderRepository riderRepository, 
                          OrderRepository orderRepository,
                          PasswordEncoder passwordEncoder) {
        this.riderRepository = riderRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RiderDTO register(Rider rider) {
        // 检查是否已存在相同手机号的骑手
        if (riderRepository.findByPhone(rider.getPhone()).isPresent()) {
            throw new RuntimeException("该手机号已被注册");
        }

        // 设置初始状态
        rider.setPassword(passwordEncoder.encode(rider.getPassword()));
        rider.setStatus(RiderStatus.OFFLINE);
        rider.setIdCard("未设置");
        rider.setIsSmartDispatch(true);
        rider.setServiceRating(5.0);
        rider.setTotalOrders(0);
        rider.setCompletedOrders(0);
        rider.setActiveOrders(0);

        // 保存骑手信息
        Rider savedRider = riderRepository.save(rider);
        System.out.println("注册骑手成功 - ID: " + savedRider.getId() + ", 手机号: " + savedRider.getPhone());
        return convertToDTO(savedRider);
    }

    @Override
    public RiderDTO login(String phone, String password) {
        Rider rider = riderRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
            
        if (!passwordEncoder.matches(password, rider.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        return convertToDTO(rider);
    }

    @Override
    public List<Order> getAcceptedOrders(Integer riderId) {
        System.out.println("获取骑手已接订单 - 骑手ID: " + riderId);
        List<Order> orders = orderRepository.findByRiderIdAndStatusIn(
            riderId,
            Arrays.asList(OrderStatus.ACCEPTED, OrderStatus.PICKED)
        );
        System.out.println("找到订单数量: " + orders.size());
        return orders;
    }

    @Override
    public List<Order> getRecommendedOrders(Integer riderId, Double latitude, Double longitude) {
        System.out.println("开始获取推荐订单 - 骑手ID: " + riderId + ", 位置: (" + latitude + ", " + longitude + ")");
        
        // 先检查数据库中所有订单的状态
        List<Order> allOrders = orderRepository.findAll();
        System.out.println("\n数据库中所有订单状态：");
        allOrders.forEach(order -> {
            System.out.println("订单ID: " + order.getId());
            System.out.println("  状态: " + order.getStatus());
            System.out.println("  骑手ID: " + order.getRiderId());
            System.out.println("  商家名称: " + order.getShop().getName());
            System.out.println("  配送地址: " + order.getDeliveryAddress());
            System.out.println("  创建时间: " + order.getCreatedAt());
            System.out.println("----------------------------------------");
        });
        
        // 获取等待骑手接单的订单
        List<Order> waitingOrders = orderRepository.findOrdersByFilters(OrderStatus.WAITING_RIDER, null, null);
        System.out.println("\n找到等待接单的订单数量: " + waitingOrders.size());
        
        if (waitingOrders.isEmpty()) {
            System.out.println("没有找到等待接单的订单");
            return waitingOrders;
        }
        
        // 获取骑手信息
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
            
        // 更新骑手位置
        rider.setCurrentLatitude(latitude);
        rider.setCurrentLongitude(longitude);
        riderRepository.save(rider);
        
        // 打印每个等待接单的订单信息
        System.out.println("\n等待接单的订单详情：");
        waitingOrders.forEach(order -> {
            System.out.println("订单ID: " + order.getId());
            System.out.println("  商家名称: " + order.getShop().getName());
            System.out.println("  商家地址: " + order.getShop().getAddress());
            System.out.println("  订单状态: " + order.getStatus());
            System.out.println("  配送地址: " + order.getDeliveryAddress());
            System.out.println("  创建时间: " + order.getCreatedAt());
            System.out.println("----------------------------------------");
        });
        
        // 暂时返回所有等待接单的订单，不进行过滤
        System.out.println("\n返回所有等待接单的订单，数量: " + waitingOrders.size());
        return waitingOrders;
    }

    @Override
    @Transactional
    public Order acceptOrder(Integer riderId, Integer orderId) {
        System.out.println("\n开始抢单处理 - 骑手ID: " + riderId + ", 订单ID: " + orderId);
        
        // 使用悲观锁锁定订单
        Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        System.out.println("订单信息:");
        System.out.println("  状态: " + order.getStatus());
        System.out.println("  商家: " + order.getShop().getName());
        System.out.println("  配送地址: " + order.getDeliveryAddress());
        System.out.println("  当前骑手ID: " + order.getRiderId());
            
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
        System.out.println("骑手信息:");
        System.out.println("  状态: " + rider.getStatus());
        System.out.println("  活跃订单数: " + rider.getActiveOrderCount());
        System.out.println("  位置: (" + rider.getCurrentLatitude() + ", " + rider.getCurrentLongitude() + ")");
            
        // 验证订单状态
        if (order.getStatus() != OrderStatus.WAITING_RIDER) {
            System.out.println("❌ 抢单失败：订单状态不是等待接单状态");
            throw new RuntimeException("订单已被其他骑手接取");
        }
        
        try {
            // 验证骑手状态和接单数量
            validateRiderForOrder(rider);
        } catch (Exception e) {
            System.out.println("❌ 抢单失败：骑手状态验证未通过 - " + e.getMessage());
            throw e;
        }
        
        try {
            // 更新订单状态为"accepted"
            order.setStatus(OrderStatus.ACCEPTED);
            order.setRiderId(riderId);
            order.setRiderName(rider.getName());
            order.setRiderPhone(rider.getPhone());
            order.setRiderLatitude(rider.getCurrentLatitude());
            order.setRiderLongitude(rider.getCurrentLongitude());
            order.setRiderLocation(rider.getCurrentLocation());
            
            // 更新骑手状态
            rider.incrementActiveOrders();
            rider.setStatus(RiderStatus.DELIVERING);
            
            // 保存更改
            System.out.println("正在保存订单状态变更...");
            System.out.println("  新状态: " + OrderStatus.ACCEPTED);
            Order savedOrder = orderRepository.save(order);
            System.out.println("订单状态已更新");
            
            System.out.println("正在保存骑手状态变更...");
            riderRepository.save(rider);
            System.out.println("骑手状态已更新");
            
            System.out.println("✅ 抢单成功！");
            System.out.println("  订单新状态: " + savedOrder.getStatus());
            System.out.println("  骑手新状态: " + rider.getStatus());
            System.out.println("  骑手当前活跃订单数: " + rider.getActiveOrderCount());
            
            return savedOrder;
        } catch (Exception e) {
            System.out.println("❌ 保存更改时发生错误: " + e.getMessage());
            throw new RuntimeException("保存订单状态时发生错误: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Order pickupOrder(Integer riderId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
            
        if (!order.getRiderId().equals(riderId)) {
            throw new RuntimeException("订单不属于该骑手");
        }
        
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(OrderStatus.PICKED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = false)  // 确保是可写事务
    public Order completeOrder(Integer riderId, Integer orderId) {
        System.out.println("\n开始处理订单完成 - 骑手ID: " + riderId + ", 订单ID: " + orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        System.out.println("订单信息:");
        System.out.println("  状态: " + order.getStatus());
        System.out.println("  商家: " + order.getShop().getName());
        System.out.println("  配送地址: " + order.getDeliveryAddress());
            
        if (!order.getRiderId().equals(riderId)) {
            System.out.println("❌ 完成订单失败：订单不属于该骑手");
            throw new RuntimeException("订单不属于该骑手");
        }
        
        if (order.getStatus() != OrderStatus.PICKED) {
            System.out.println("❌ 完成订单失败：订单状态不是已取餐状态");
            throw new RuntimeException("订单状态不正确");
        }
        
        try {
            // 更新订单状态为"completed"
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedTime(LocalDateTime.now());
            
            // 更新骑手状态
            Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("骑手不存在"));
            rider.decrementActiveOrders();
            rider.setCompletedOrders(rider.getCompletedOrders() + 1);
            
            // 保存更改
            System.out.println("正在保存订单状态变更...");
            System.out.println("  新状态: " + OrderStatus.COMPLETED);
            Order savedOrder = orderRepository.save(order);
            System.out.println("订单状态已更新");
            
            System.out.println("正在保存骑手状态变更...");
            riderRepository.save(rider);
            System.out.println("骑手状态已更新");
            
            System.out.println("✅ 订单完成！");
            System.out.println("  订单新状态: " + savedOrder.getStatus());
            System.out.println("  骑手新状态: " + rider.getStatus());
            System.out.println("  骑手当前活跃订单数: " + rider.getActiveOrderCount());
            System.out.println("  骑手完成订单总数: " + rider.getCompletedOrders());
            
            return savedOrder;
        } catch (Exception e) {
            System.out.println("❌ 保存更改时发生错误: " + e.getMessage());
            throw new RuntimeException("保存订单状态时发生错误: " + e.getMessage());
        }
    }

    @Override
    public List<Order> getFilteredOrders(Integer riderId, String merchantName, String userAddress) {
        return orderRepository.findOrdersByFilters(OrderStatus.WAITING_RIDER, merchantName, userAddress);
    }

    @Override
    public Order getOrderDetail(Integer riderId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
            
        if (!order.getRiderId().equals(riderId)) {
            throw new RuntimeException("订单不属于该骑手");
        }
        
        return order;
    }

    @Override
    public Page<Order> getRiderOrders(Integer riderId, Pageable pageable) {
        return orderRepository.findByRiderId(riderId, pageable);
    }

    @Override
    public RiderDTO updateStatus(Integer riderId, RiderStatus status) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
        rider.setStatus(status);
        return convertToDTO(riderRepository.save(rider));
    }

    @Override
    public RiderDTO updateLocation(Integer riderId, Double latitude, Double longitude, String location) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
            
        rider.setCurrentLatitude(latitude);
        rider.setCurrentLongitude(longitude);
        rider.setCurrentLocation(location);
        
        return convertToDTO(riderRepository.save(rider));
    }

    @Override
    public void processSmartDispatch(Order order) {
        if (!order.getIsSmartAssignment()) {
            return;
        }
        
        List<Rider> nearbyRiders = findNearbyRiders(
            order.getShop().getLatitude(),
            order.getShop().getLongitude(),
            MAX_REASONABLE_DISTANCE
        );
        
        if (nearbyRiders.isEmpty()) {
            return;
        }
        
        // 计算每个骑手的得分
        Map<Rider, Double> riderScores = new HashMap<>();
        for (Rider rider : nearbyRiders) {
            double score = calculateRiderScore(rider, order);
            riderScores.put(rider, score);
        }
        
        // 选择得分最高的骑手
        Optional<Map.Entry<Rider, Double>> bestRider = riderScores.entrySet().stream()
            .max(Map.Entry.comparingByValue());
            
        bestRider.ifPresent(entry -> {
            Rider rider = entry.getKey();
            assignOrderToRider(order, rider);
        });
    }

    @Override
    public List<Rider> findNearbyRiders(Double latitude, Double longitude, Double maxDistance) {
        return riderRepository.findAll().stream()
            .filter(rider -> {
                if (rider.getCurrentLatitude() == null || rider.getCurrentLongitude() == null) {
                    return false;
                }
                double distance = calculateDistance(
                    latitude, longitude,
                    rider.getCurrentLatitude(), rider.getCurrentLongitude()
                );
                return distance <= maxDistance;
            })
            .collect(Collectors.toList());
    }

    private boolean isOrderSuitableForRider(Order order, Integer riderId) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
            
        // 检查骑手状态
        if (rider.getStatus() != RiderStatus.ONLINE) {
            return false;
        }
        
        // 检查骑手当前订单数
        if (rider.getActiveOrderCount() >= MAX_CONCURRENT_ORDERS) {
            return false;
        }
        
        // 检查距离
        if (rider.getCurrentLatitude() == null || rider.getCurrentLongitude() == null) {
            return false;
        }
        
        double distance = calculateDistance(
            order.getShop().getLatitude(), order.getShop().getLongitude(),
            rider.getCurrentLatitude(), rider.getCurrentLongitude()
        );
        
        return distance <= MAX_REASONABLE_DISTANCE;
    }

    private int compareOrderPriority(Order o1, Order o2, Double riderLat, Double riderLng) {
        double score1 = calculateOrderScore(o1, riderLat, riderLng);
        double score2 = calculateOrderScore(o2, riderLat, riderLng);
        return Double.compare(score2, score1); // 分数高的优先
    }

    private double calculateOrderScore(Order order, Double riderLat, Double riderLng) {
        double distanceScore = calculateDistanceScore(order, riderLat, riderLng);
        double waitingTimeScore = calculateWaitingTimeScore(order);
        double priceScore = calculatePriceScore(order);
        
        return distanceScore * DISTANCE_WEIGHT +
               waitingTimeScore * RATING_WEIGHT +
               priceScore * EXPERIENCE_WEIGHT;
    }

    private double calculateDistanceScore(Order order, Double riderLat, Double riderLng) {
        double distance = calculateDistance(
            order.getShop().getLatitude(), order.getShop().getLongitude(),
            riderLat, riderLng
        );
        return Math.max(0, 1 - (distance / MAX_REASONABLE_DISTANCE));
    }

    private double calculateWaitingTimeScore(Order order) {
        long waitingMinutes = ChronoUnit.MINUTES.between(order.getCreatedAt(), LocalDateTime.now());
        return Math.max(0, 1 - (waitingMinutes / 30.0)); // 30分钟为基准
    }

    private double calculatePriceScore(Order order) {
        return Math.min(1.0, order.getTotalAmount().doubleValue() / 100.0); // 100元为基准
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径，单位：公里
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private void validateRiderForOrder(Rider rider) {
        // 检查骑手状态
        if (rider.getStatus() != RiderStatus.ONLINE && rider.getStatus() != RiderStatus.DELIVERING) {
            throw new RuntimeException("骑手当前状态不可接单");
        }
        
        // 检查骑手当前订单数
        if (rider.getActiveOrderCount() >= MAX_CONCURRENT_ORDERS) {
            throw new RuntimeException("骑手当前订单数已达上限");
        }
        
        // 检查骑手位置信息
        if (rider.getCurrentLatitude() == null || rider.getCurrentLongitude() == null) {
            throw new RuntimeException("骑手位置信息不完整");
        }
    }

    private void assignOrderToRider(Order order, Rider rider) {
        order.setStatus(OrderStatus.ACCEPTED);
        order.setRiderId(rider.getId());
        order.setRiderName(rider.getName());
        order.setRiderPhone(rider.getPhone());
        order.setRiderLatitude(rider.getCurrentLatitude());
        order.setRiderLongitude(rider.getCurrentLongitude());
        order.setRiderLocation(rider.getCurrentLocation());
        
        rider.incrementActiveOrders();
        rider.setStatus(RiderStatus.DELIVERING);
        
        orderRepository.save(order);
        riderRepository.save(rider);
    }

    private RiderDTO convertToDTO(Rider rider) {
        RiderDTO dto = new RiderDTO();
        dto.setId(rider.getId());
        dto.setName(rider.getName());
        dto.setPhone(rider.getPhone());
        dto.setIdCard(rider.getIdCard());
        dto.setStatus(rider.getStatus());
        dto.setCurrentLatitude(rider.getCurrentLatitude());
        dto.setCurrentLongitude(rider.getCurrentLongitude());
        dto.setCurrentLocation(rider.getCurrentLocation());
        dto.setIsSmartDispatch(rider.getIsSmartDispatch());
        dto.setServiceRating(rider.getServiceRating());
        dto.setTotalOrders(rider.getTotalOrders());
        dto.setCompletedOrders(rider.getCompletedOrders());
        dto.setActiveOrderCount(rider.getActiveOrderCount());
        return dto;
    }

    private double calculateRiderScore(Rider rider, Order order) {
        // 距离得分
        double distanceScore = 0;
        if (rider.getCurrentLatitude() != null && rider.getCurrentLongitude() != null) {
            double distance = calculateDistance(
                order.getShop().getLatitude(), order.getShop().getLongitude(),
                rider.getCurrentLatitude(), rider.getCurrentLongitude()
            );
            distanceScore = Math.max(0, 1 - (distance / MAX_REASONABLE_DISTANCE));
        }
        
        // 评分得分
        double ratingScore = rider.getServiceRating() / 5.0;
        
        // 经验得分
        double experienceScore = Math.min(1.0, rider.getCompletedOrders() / (double)EXPERIENCE_BENCHMARK);
        
        // 负载得分
        double loadScore = 1 - (rider.getActiveOrderCount() / (double)MAX_CONCURRENT_ORDERS);
        
        return distanceScore * DISTANCE_WEIGHT +
               ratingScore * RATING_WEIGHT +
               experienceScore * EXPERIENCE_WEIGHT +
               loadScore * LOAD_WEIGHT;
    }

    @Override
    public RiderDTO getRiderById(Integer riderId) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
        return convertToDTO(rider);
    }

    @Override
    public RiderDTO getRiderByPhone(String phone) {
        Rider rider = riderRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
        return convertToDTO(rider);
    }

    @Override
    @Transactional
    public RiderDTO updateProfile(Integer riderId, RiderDTO riderDTO) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
            
        rider.setName(riderDTO.getName());
        rider.setIdCard(riderDTO.getIdCard());
        rider.setIsSmartDispatch(riderDTO.getIsSmartDispatch());
        
        return convertToDTO(riderRepository.save(rider));
    }

    @Override
    public Map<String, Object> getRiderStatistics(Integer riderId) {
        Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("骑手不存在"));
            
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", rider.getTotalOrders());
        statistics.put("completedOrders", rider.getCompletedOrders());
        statistics.put("activeOrders", rider.getActiveOrderCount());
        statistics.put("serviceRating", rider.getServiceRating());
        
        // 计算今日完成订单数
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long todayCompletedOrders = orderRepository.countByRiderIdAndStatusAndCompletedTimeBetween(
            riderId, OrderStatus.COMPLETED, today, LocalDateTime.now()
        );
        statistics.put("todayCompletedOrders", todayCompletedOrders);
        
        return statistics;
    }
} 