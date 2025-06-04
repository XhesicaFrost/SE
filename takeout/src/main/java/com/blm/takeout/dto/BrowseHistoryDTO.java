package com.blm.takeout.dto;

import com.blm.takeout.entity.BrowseHistory.TargetType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BrowseHistoryDTO {
    private Integer id;
    private Integer userId;
    private TargetType targetType;
    private Integer targetId;
    private LocalDateTime browseTime;
    
    // 店铺或商品的基本信息
    private String name;
    private String image;
    private String description;
} 