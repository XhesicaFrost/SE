package com.blm.takeout.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Integer userId;
    private Integer itemId;
    private Integer orderId;
    private Integer rating;
    private String comment;
    private String images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 