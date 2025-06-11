package com.blm.takeout.service.impl;

import com.blm.takeout.dto.PaymentDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Address;
import com.blm.takeout.entity.CartItem;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.OrderItem;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.CartItemRepository;
import com.blm.takeout.repository.AddressRepository;
import com.blm.takeout.repository.OrderItemRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.service.PaymentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Override
    @Transactional
    public boolean processPayment(PaymentDTO paymentDTO) {
        try {
            System.err.println("Function init");
            // 1. 获取用户当前地址
            Optional<Address> currentAddressOpt = addressRepository.findByUserIdAndCurrentTrue(paymentDTO.getUserId());
            Address currentAddress;
            
            if (currentAddressOpt.isPresent()) {
                // 情况3：正常获取到用户的当前地址
                currentAddress = currentAddressOpt.get();
                System.err.println("找到用户当前地址");
            } else {
                // 情况2：用户有地址但没有current=true的地址
                List<Address> userAddresses = addressRepository.findByUserId(paymentDTO.getUserId());
                if (userAddresses.isEmpty()) {
                    // 情况1：用户没有地址
                    throw new RuntimeException("未找到用户地址");
                }
                // 将第一个地址设置为当前地址
                currentAddress = userAddresses.get(0);
                currentAddress.setCurrent(true);
                addressRepository.save(currentAddress);
                System.err.println("已将用户第一个地址设置为当前地址");
            }
            
            System.err.println("User Address Get Success");
            // 2. 获取商家信息
            Shop shop = shopRepository.findById(paymentDTO.getShopId())
                .orElseThrow(() -> new RuntimeException("未找到商家信息"));
            System.err.println("Shop Get Success");
            // 3. 创建订单
            Order order = new Order();
            order.setUser(userRepository.findById(paymentDTO.getUserId()).orElseThrow());
            order.setShop(shop);
            order.setStatus(Order.OrderStatus.PREPARING);
            order.setDeliveryAddress(currentAddress.getFullAddress());
            order.setDeliveryLatitude(shop.getLatitude());
            order.setDeliveryLongitude(shop.getLongitude());
            order.setDeliveryPhone(currentAddress.getPhone());
            order.setDeliveryName(currentAddress.getName());
            
            // 设置时间字段
            LocalDateTime now = LocalDateTime.now();
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            order.setCreateTime(now);
            order.setUpdateTime(now);
            
            System.err.println("Order create success");
            // 计算总金额
            ObjectMapper mapper = new ObjectMapper();
            BigDecimal totalAmount = paymentDTO.getItems().stream()
                .map(item -> {
                    Map<String, Object> product = mapper.convertValue(item.get("product"), new TypeReference<Map<String, Object>>() {});
                    double price = ((Number) product.get("price")).doubleValue();
                    int quantity = ((Number) item.get("quantity")).intValue();
                    return BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(totalAmount);
            
            // 生成订单号
            order.setOrderNumber(generateOrderNumber());
            
            // 保存订单
            orderRepository.save(order);
            
            // 创建订单项
            List<OrderItem> orderItems = paymentDTO.getItems().stream()
                .map(item -> {
                    Map<String, Object> product = mapper.convertValue(item.get("product"), new TypeReference<Map<String, Object>>() {});
                    Integer itemId = ((Number) product.get("id")).intValue();
                    int quantity = ((Number) item.get("quantity")).intValue();
                    double price = ((Number) product.get("price")).doubleValue();
                    Item itemEntity = itemRepository.findById(itemId)
                        .orElseThrow(() -> new BusinessException("商品不存在"));
                    itemEntity.setSales(itemEntity.getSales() + quantity);
                    itemRepository.save(itemEntity);
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setItem(itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("商品不存在")));
                    orderItem.setQuantity(quantity);
                    orderItem.setUnitPrice(BigDecimal.valueOf(price));
                    orderItem.setTotalPrice(BigDecimal.valueOf(price * quantity));
                    
                    return orderItem;
                })
                .collect(Collectors.toList());
            
            // 保存订单项
            orderItemRepository.saveAll(orderItems);
            
            // 4. 删除购物车中已选中的商品
            System.err.println("开始删除购物车商品，用户ID: " + paymentDTO.getUserId() + ", 商家ID: " + paymentDTO.getShopId());
            List<CartItem> cartItems = cartItemRepository.findByUserIdAndShopId(paymentDTO.getUserId(), paymentDTO.getShopId());
            System.err.println("找到购物车商品数量: " + cartItems.size());
            
            // 直接删除该商家的所有购物车项
            cartItems.forEach(cartItem -> {
                System.err.println("正在删除购物车商品ID: " + cartItem.getId());
                cartItemRepository.delete(cartItem);
            });
            System.err.println("CartItem delete success");
            // 模拟支付成功
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 即使发生异常也返回支付成功
            return true;
        }
    }
    
    private String generateOrderNumber() {
        // 生成订单号：时间戳 + 6位随机数
        return System.currentTimeMillis() + String.format("%06d", new Random().nextInt(1000000));
    }
} 