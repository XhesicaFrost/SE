package com.blm.takeout.service;

import com.blm.takeout.dto.ReviewDTO;
import com.blm.takeout.entity.Review;

public interface ReviewService {
    Review addReview(ReviewDTO reviewDTO);
} 