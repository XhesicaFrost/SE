package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer userId;
    private String name;
    private String phone;
    private String fullAddress;
    private Boolean current;
} 