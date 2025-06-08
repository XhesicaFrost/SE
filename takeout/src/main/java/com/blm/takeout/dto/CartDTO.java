package com.blm.takeout.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CartDTO {
    private Map<String, Object> shop;
    private List<CartItemDTO> items;
} 