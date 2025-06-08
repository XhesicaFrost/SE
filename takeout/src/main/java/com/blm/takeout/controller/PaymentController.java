package com.blm.takeout.controller;

import com.blm.takeout.dto.PaymentDTO;
import com.blm.takeout.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> handlePayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            System.out.println("收到支付请求：" + paymentDTO);
            
            // 模拟支付处理
            boolean success = paymentService.processPayment(paymentDTO);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("code", 200);
                response.put("message", "支付成功");
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