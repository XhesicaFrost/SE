package com.blm.takeout.dto;

import java.util.List;
import java.util.Map;

public class PaymentDTO {
    private Integer userId;
    private Integer shopId;
    private List<Map<String, Object>> items;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
} 