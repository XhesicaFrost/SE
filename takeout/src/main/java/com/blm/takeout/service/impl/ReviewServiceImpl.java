package com.blm.takeout.service.impl;

import com.blm.takeout.dto.ReviewDTO;
import com.blm.takeout.entity.Review;
import com.blm.takeout.repository.ReviewRepository;
import com.blm.takeout.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.UserRepository;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setUserId(reviewDTO.getUserId());
        review.setItemId(reviewDTO.getItemId());
        review.setOrderId(reviewDTO.getOrderId());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setImages(reviewDTO.getImages());
        
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    @Override
    public Page<ReviewDTO> getItemReviews(Integer itemId, Pageable pageable) {
        return reviewRepository.findByItemIdOrderByCreatedAtDesc(itemId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ReviewDTO> getUserReviews(Integer userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Integer userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评价");
        }
        
        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public void adminDeleteReview(Long reviewId, Integer adminId) {
        // 验证管理员存在
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        // 删除评价
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        
        reviewRepository.delete(review);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setItemId(review.getItemId());
        dto.setOrderId(review.getOrderId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setImages(review.getImages());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
} 