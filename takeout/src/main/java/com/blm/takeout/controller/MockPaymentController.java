package com.blm.takeout.controller;

import com.blm.takeout.service.WechatPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
public class MockPaymentController {
    private final WechatPayService wechatPayService;

    public MockPaymentController(WechatPayService wechatPayService) {
        this.wechatPayService = wechatPayService;
    }

    @GetMapping("/mock")
    public ResponseEntity<String> mockPayment(
            @RequestParam String paymentId,
            @RequestParam Double amount) {
        // 模拟支付页面
        String html = String.format("""
            <html>
            <body>
                <h1>模拟支付页面</h1>
                <p>订单金额: %.2f</p>
                <p>支付ID: %s</p>
                <form action="/api/pay/mock/confirm" method="post">
                    <input type="hidden" name="paymentId" value="%s">
                    <button type="submit">确认支付</button>
                </form>
            </body>
            </html>
            """, amount, paymentId, paymentId);
        
        return ResponseEntity.ok(html);
    }

    @PostMapping("/mock/confirm")
    public ResponseEntity<String> confirmPayment(@RequestParam String paymentId) {
        boolean success = wechatPayService.mockPay(paymentId);
        if (success) {
            return ResponseEntity.ok("支付成功！");
        } else {
            return ResponseEntity.badRequest().body("支付失败！");
        }
    }
} 