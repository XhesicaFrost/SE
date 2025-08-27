package com.blm.takeout.controller;

import com.blm.takeout.dto.PaymentDTO;
import com.blm.takeout.service.PaymentService;
import com.blm.takeout.entity.Promotion;
import com.blm.takeout.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PromotionRepository promotionRepository;

    @PostMapping("/payment")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> handlePayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            System.out.println("收到支付请求：" + paymentDTO);
            
            // 计算原价
            double originalPrice = paymentService.calculateOriginalPrice(paymentDTO);
            
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            
            // 查询该商家的所有促销活动
            List<Promotion> promotions = promotionRepository.findBySellerId(paymentDTO.getShopId());
            
            // 找出满足条件的最大优惠
            double maxDiscount = 0;
            for (Promotion promotion : promotions) {
                if (now.isAfter(promotion.getStartTime()) && 
                    now.isBefore(promotion.getEndTime()) && 
                    originalPrice >= promotion.getFull()) {
                    maxDiscount = Math.max(maxDiscount, promotion.getMinus());
                }
            }
            
            // 计算最终价格
            double finalPrice = originalPrice - maxDiscount;
            
            // 模拟支付处理
            boolean success = paymentService.processPayment(paymentDTO);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("code", 200);
                response.put("message", "支付成功");
                response.put("originalPrice", originalPrice);
                response.put("discountedPrice", finalPrice);
                System.out.println("支付成功");
            } else {
                response.put("code", 500);
                response.put("message", "支付失败");
                System.out.println("支付失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("支付处理异常：" + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 