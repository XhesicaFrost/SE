package com.blm.takeout.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer id;
    private Integer itemId;
    private String itemName;
    private String itemImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
} 