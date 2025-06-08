package com.blm.takeout.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CartItemDTO {
    private Map<String, Object> product;
    private Integer quantity;
} 