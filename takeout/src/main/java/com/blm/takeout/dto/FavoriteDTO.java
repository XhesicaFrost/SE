package com.blm.takeout.dto;

import com.blm.takeout.entity.Favorite.TargetType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteDTO {
    private Integer id;
    private Integer userId;
    private TargetType targetType;
    private Integer targetId;
    private LocalDateTime createdAt;
    
    // 店铺或商品的基本信息
    private String name;
    private String image;
    private String description;
    private Double rating;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
} 