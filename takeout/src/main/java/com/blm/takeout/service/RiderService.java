package com.blm.takeout.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Rider;
import com.blm.takeout.entity.Order.OrderStatus;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiderService {
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void updateLocaction(Integer userId, Double latitude, Double longtitude) throws Exception {
        Rider rider = riderRepository.findByUser_Userid(userId)
                .orElseThrow(() -> new BusinessException("骑手不存在"));
        rider.setCurrentLatitude(latitude);
        rider.setCurrentLongitude(longtitude);
        riderRepository.save(rider);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAcceptedOrders(Integer riderId) {
        List<Order> orders = orderRepository.findByRiderIdAndStatusIn(
            riderId, List.of(Order.OrderStatus.PICKED, Order.OrderStatus.ACCEPTED)
        );
        return orders.stream()
            .map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("sellerName", order.getShop().getName());
                map.put("sellerAddress", order.getShop().getAddress());
                map.put("userAddress", order.getDeliveryAddress());
                map.put("createTime", order.getCreateTime().toString());
                map.put("status", order.getStatus().name());
                return map;
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecommendedOrders(Integer riderId, Double latitude, Double longitude) {
        List<Order> orders = orderRepository.findReadyOrders();
        if (orders.size() < 2) {
            return orders.stream()
                    .map(order -> {
                        double distance = calculateDistance(latitude, longitude,
                                order.getShop().getLatitude(), order.getShop().getLongitude());
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", order.getId());
                        map.put("sellerName", order.getShop().getName());
                        map.put("sellerAddress", order.getShop().getAddress());
                        map.put("userAddress", order.getDeliveryAddress());
                        map.put("createTime", order.getCreateTime().toString());
                        map.put("distance", distance);
                        return map;
                    })
                    .sorted((o1, o2) -> Double.compare((Double) o1.get("distance"), (Double) o2.get("distance")))
                    .limit(10)
                    .toList();
        }

        int n = orders.size();
        double[][] graph = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    graph[i][j] = calculateDistance(
                            orders.get(i).getShop().getLatitude(), orders.get(i).getShop().getLongitude(),
                            orders.get(j).getShop().getLatitude(), orders.get(j).getShop().getLongitude()
                    );
                } else {
                    graph[i][j] = Double.MAX_VALUE; 
                }
            }
        }

        List<Integer> shortestPath = findShortestHamiltonianPath(graph);

        return shortestPath.stream()
                .map(index -> {
                    Order order = orders.get(index);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", order.getId());
                    map.put("sellerName", order.getShop().getName());
                    map.put("sellerAddress", order.getShop().getAddress());
                    map.put("userAddress", order.getDeliveryAddress());
                    map.put("createTime", order.getCreateTime().toString());
                    return map;
                })
                .toList();
    }

    private List<Integer> findShortestHamiltonianPath(double[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        List<Integer> currentPath = new ArrayList<>();
        List<Integer> bestPath = new ArrayList<>();
        double[] minDistance = {Double.MAX_VALUE};

        for (int start = 0; start < n; start++) {
            visited[start] = true;
            currentPath.add(start);
            backtrack(graph, visited, currentPath, bestPath, minDistance, 0, start);
            visited[start] = false;
            currentPath.remove(currentPath.size() - 1);
        }

        return bestPath;
    }

    private void backtrack(double[][] graph, boolean[] visited, List<Integer> currentPath,
                        List<Integer> bestPath, double[] minDistance, double currentDistance, int currentNode) {
        if (currentPath.size() == graph.length) {
            if (currentDistance < minDistance[0]) {
                minDistance[0] = currentDistance;
                bestPath.clear();
                bestPath.addAll(currentPath);
            }
            return;
        }

        for (int nextNode = 0; nextNode < graph.length; nextNode++) {
            if (!visited[nextNode] && graph[currentNode][nextNode] != Double.MAX_VALUE) {
                visited[nextNode] = true;
                currentPath.add(nextNode);
                backtrack(graph, visited, currentPath, bestPath, minDistance,
                        currentDistance + graph[currentNode][nextNode], nextNode);
                visited[nextNode] = false;
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    /**
     * 使用 Haversine 公式计算两点之间的距离（单位：公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 地球半径，单位：公里

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    @Transactional
    public boolean updateOrderStatus(Integer riderId, Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        // 验证订单是否属于该骑手
        if (!order.getRiderId().equals(riderId)) {
            throw new BusinessException("该订单不属于当前骑手");
        }

        // 更新订单状态
        switch (status.toLowerCase()) {
            case "picked":
                order.setStatus(Order.OrderStatus.PICKED);
                break;
            case "completed":
                order.setStatus(Order.OrderStatus.COMPLETED);
                order.setCompletedTime(LocalDateTime.now());
                break;
            default:
                throw new BusinessException("无效的状态: " + status);
        }

        orderRepository.save(order);
        return true;
    }

    @Transactional
    public boolean chooseOrder(Integer riderId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        // 检查订单状态是否为 READY
        if (!order.getStatus().equals(Order.OrderStatus.READY)) {
            return false; // 订单已被其他骑手接取
        }

        // 更新订单状态和骑手信息
        order.setRiderId(riderId);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        return true;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderDetail(Integer riderId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!order.getRiderId().equals(riderId)) {
            throw new BusinessException("订单不存在或无权限访问");
        }

        Map<String, Object> orderDetail = new HashMap<>();
        orderDetail.put("id", order.getId());
        orderDetail.put("sellerName", order.getShop().getName());
        orderDetail.put("sellerAddress", order.getShop().getAddress());
        orderDetail.put("sellerLng", order.getShop().getLongitude());
        orderDetail.put("sellerLat", order.getShop().getLatitude());
        orderDetail.put("userAddress", order.getDeliveryAddress());
        orderDetail.put("userLng", order.getDeliveryLongitude());
        orderDetail.put("userLat", order.getDeliveryLatitude());
        orderDetail.put("userPhone", order.getDeliveryPhone());
        orderDetail.put("createTime", order.getCreateTime().toString());
        orderDetail.put("status", order.getStatus().name());

        return orderDetail;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHistoryOrders(Integer userId, Integer page, Integer pageSize) {
        // 分页查询历史订单
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "completedTime"));
        Page<Order> orderPage = orderRepository.findByRiderIdAndStatus(userId, Order.OrderStatus.COMPLETED, pageable);

        List<Map<String, Object>> orders = orderPage.getContent().stream()
                .map(order -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", order.getId());
                    map.put("sellerName", order.getShop().getName());
                    map.put("sellerAddress", order.getShop().getAddress());
                    map.put("userAddress", order.getDeliveryAddress());
                    map.put("userPhone", order.getDeliveryPhone());
                    map.put("createTime", order.getCreateTime().toString());
                    map.put("completeTime", order.getCompletedTime() != null ? order.getCompletedTime().toString() : null);
                    return map;
                })
                .toList();

        return Map.of(
                "orders", orders,
                "total", orderPage.getTotalElements(),
                "page", page,
                "pageSize", pageSize
        );
    }
}
