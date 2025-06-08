package com.blm.takeout.dto;

import com.blm.takeout.entity.BrowseHistory.TargetType;
import lombok.Data;

@Data
public class BrowseHistoryDTO {
    private String userId;
    private TargetType targetType;
    private String targetId;
    private String name;
    private String image;
    private String description;
} 