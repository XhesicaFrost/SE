package com.blm.takeout.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long userId;
    private Integer shopId;
    private Long orderId;
    private String type;
    private String detail;
    private MultipartFile image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 