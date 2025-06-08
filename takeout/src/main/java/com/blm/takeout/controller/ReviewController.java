package com.blm.takeout.controller;

import com.blm.takeout.dto.ReviewDTO;
import com.blm.takeout.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评价成功");
            response.put("data", createdReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> getItemReviews(
            @PathVariable Integer itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ReviewDTO> reviews = reviewService.getItemReviews(itemId, PageRequest.of(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserReviews(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ReviewDTO> reviews = reviewService.getUserReviews(userId, PageRequest.of(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Integer userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评价删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "删除评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/admin/{reviewId}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Map<String, Object>> adminDeleteReview(
            @PathVariable Long reviewId,
            @RequestParam Integer adminId) {
        try {
            reviewService.adminDeleteReview(reviewId, adminId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评价删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "删除评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 