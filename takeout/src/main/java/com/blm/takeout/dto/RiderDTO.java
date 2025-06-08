package com.blm.takeout.dto;

import com.blm.takeout.entity.Rider.RiderStatus;
import lombok.Data;

@Data
public class RiderDTO {
    private Integer id;
    private String name;
    private String phone;
    private String idCard;
    private RiderStatus status;
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentLocation;
    private Boolean isSmartDispatch;
    private Double serviceRating;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer activeOrderCount;
} 