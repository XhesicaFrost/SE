package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    private Integer itemId;
    private String itemName;
    private String itemDescription;
    private String itemImage;
    private Double unitPrice;
    private Integer quantity;
} 