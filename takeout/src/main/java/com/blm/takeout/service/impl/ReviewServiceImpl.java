package com.blm.takeout.service.impl;

import com.blm.takeout.dto.ReviewDTO;
import com.blm.takeout.entity.Review;
import com.blm.takeout.entity.Order;
import com.blm.takeout.repository.ReviewRepository;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.service.ReviewService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public Review addReview(ReviewDTO reviewDTO) {
        // 验证订单是否存在
        Order order = orderRepository.findById(reviewDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 创建评论实体
        Review review = new Review();
        review.setUserId(reviewDTO.getUserId());
        review.setShopId(order.getShop().getId());
        review.setOrderId(reviewDTO.getOrderId());
        review.setType(reviewDTO.getType());
        review.setDetail(reviewDTO.getDetail());

        // 处理图片
        if (reviewDTO.getImage() != null && !reviewDTO.getImage().isEmpty()) {
            try {
                String imagePath = FileUtils.saveImage(reviewDTO.getImage());
                review.setImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("图片保存失败", e);
            }
        }

        return reviewRepository.save(review);
    }
} 