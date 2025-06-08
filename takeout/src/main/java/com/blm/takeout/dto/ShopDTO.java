package com.blm.takeout.dto;

import lombok.Data;
import java.util.List;

@Data
public class ShopDTO {
    private Integer id;
    private String image;
    private String name;
    private Double rating;
    private List<TagDTO> tags;
    private Double avgPrice;
    private Double distance;
    private Integer deliverTime;
    private List<ProductImageDTO> products;
    private String address;
    private Integer sales;
}