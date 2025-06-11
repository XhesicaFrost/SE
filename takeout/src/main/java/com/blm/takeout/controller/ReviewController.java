package com.blm.takeout.controller;

import com.blm.takeout.dto.ReviewDTO;
import com.blm.takeout.entity.Review;
import com.blm.takeout.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order/comment")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createReview(@ModelAttribute ReviewDTO reviewDTO) {
        try {
            Review review = reviewService.addReview(reviewDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "评价成功");
            response.put("data", review);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 