package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "item")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String itemname;

    @Column(nullable = false)
    private Double price;

    @Column(length = 255)
    private String description;

}
