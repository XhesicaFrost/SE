package com.blm.takeout.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer id;
    private Integer itemId;
    private Integer quantity;
    private BigDecimal price;
} 