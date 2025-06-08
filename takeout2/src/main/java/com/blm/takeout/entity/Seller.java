package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seller")
@Data
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(length = 256)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status sellerStatus;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, referencedColumnName = "userid")
    private User user;

    public enum Status {
        未注册,
        正常,
        审批中,
        封禁中
    }
}
