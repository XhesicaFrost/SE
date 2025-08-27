package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "merchant")
@Data
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String shopname;

    @Column(nullable = false)
    private String shopaddress;

    @Column
    private String shopimage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status;

    @Column(nullable = false)
    private Integer userid;

    public enum MerchantStatus {
        PENDING,
        APPROVED,
        REJECTED,
        BANNED
    }
}
