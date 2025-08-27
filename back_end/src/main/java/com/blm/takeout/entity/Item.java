package com.blm.takeout.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String image;

    @Column(nullable = false)
    private Integer sales = 0;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        审批中,
        正常,
        下架,
        封禁中;

        public static Status fromString(String text) {
            for (Status status : Status.values()) {
                if (status.name().equals(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + text);
        }
    }

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Shop shop;
} 
