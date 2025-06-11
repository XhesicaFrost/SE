package com.blm.takeout.controller;

import com.blm.takeout.entity.Review;
import com.blm.takeout.entity.ReviewStatus;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.ReviewRepository;
import com.blm.takeout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminCommentController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/shop/comments/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopComments(@PathVariable Integer shopId) {
        try {
            List<Review> reviews = reviewRepository.findByShopId(shopId);
            
            List<Map<String, Object>> formattedReviews = reviews.stream()
                .map(review -> {
                    Map<String, Object> reviewMap = new HashMap<>();
                    reviewMap.put("id", review.getId());
                    reviewMap.put("userId", review.getUserId());
                    reviewMap.put("orderId", review.getOrderId());
                    reviewMap.put("type", review.getType());
                    reviewMap.put("content", review.getDetail());
                    
                    // 将现有的image转换为base64
                    String image = review.getImage();
                    if (image != null && !image.isEmpty()) {
                        reviewMap.put("image", Base64.getEncoder().encodeToString(image.getBytes()));
                    } else {
                        reviewMap.put("image", null);
                    }
                    
                    reviewMap.put("createTime", review.getCreatedAt());
                    reviewMap.put("status", review.getStatus().getDisplayName());
                    
                    // 获取用户信息
                    userRepository.findById(review.getUserId().intValue())
                        .ifPresent(user -> {
                            reviewMap.put("username", user.getUsername());
                        });
                    
                    return reviewMap;
                })
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", formattedReviews);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取评论失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/comment/approve")
    public ResponseEntity<Map<String, Object>> approveComment(@RequestBody Map<String, Long> request) {
        try {
            Long commentId = request.get("commentId");
            Review review = reviewRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
            
            review.setStatus(ReviewStatus.APPROVED);
            reviewRepository.save(review);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评论已通过");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/comment/reject")
    public ResponseEntity<Map<String, Object>> rejectComment(@RequestBody Map<String, Long> request) {
        try {
            Long commentId = request.get("commentId");
            Review review = reviewRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
            
            review.setStatus(ReviewStatus.REJECTED);
            reviewRepository.save(review);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评论已拒绝");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 