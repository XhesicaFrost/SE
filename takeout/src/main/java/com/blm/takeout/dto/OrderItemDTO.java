package com.blm.takeout.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer id;
    private Integer itemId;
    private String itemName;
    private String itemDescription;
    private String itemImage;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
} 