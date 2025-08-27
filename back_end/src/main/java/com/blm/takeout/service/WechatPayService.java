package com.blm.takeout.service;

import com.blm.takeout.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class WechatPayService {
    @Value("${wechat.pay.appId}")
    private String appId;

    @Value("${wechat.pay.mchId}")
    private String mchId;

    @Value("${wechat.pay.apiKey}")
    private String apiKey;

    @Value("${wechat.pay.notifyUrl}")
    private String notifyUrl;

    // 模拟支付状态存储
    private final Map<String, String> paymentStatus = new HashMap<>();

    public String createPayment(Order order) {
        // 模拟创建支付订单
        String paymentId = "wx_" + order.getOrderNumber();
        paymentStatus.put(paymentId, "PENDING");
        
        // 模拟支付链接
        String payUrl = String.format("http://localhost:8080/api/pay/mock?paymentId=%s&amount=%.2f", 
            paymentId, order.getTotalAmount());
        
        return payUrl;
    }

    public boolean verifyPayment(String orderNumber, String paymentId) {
        // 模拟验证支付状态
        String status = paymentStatus.get(paymentId);
        return "PAID".equals(status);
    }

    public void handlePaymentNotify(Map<String, String> notifyData) {
        // 模拟处理支付回调
        String paymentId = notifyData.get("paymentId");
        if (paymentId != null) {
            paymentStatus.put(paymentId, "PAID");
        }
    }

    // 模拟支付接口
    public boolean mockPay(String paymentId) {
        if (paymentStatus.containsKey(paymentId)) {
            paymentStatus.put(paymentId, "PAID");
            return true;
        }
        return false;
    }
} 