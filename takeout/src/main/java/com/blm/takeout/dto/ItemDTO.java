package com.blm.takeout.dto;

import lombok.Data;

@Data
public class ItemDTO {
    private Integer id;
    private String image;
    private String name;
    private String description;
    private Double price;
} 