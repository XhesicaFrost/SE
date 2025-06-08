package com.blm.takeout.service;

import com.blm.takeout.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);
    Page<ReviewDTO> getItemReviews(Integer itemId, Pageable pageable);
    Page<ReviewDTO> getUserReviews(Integer userId, Pageable pageable);
    void deleteReview(Long reviewId, Integer userId);
    void adminDeleteReview(Long reviewId, Integer adminId);
} 