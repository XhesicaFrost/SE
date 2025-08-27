package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "riders")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "current_location")
    private String currentLocation;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, referencedColumnName = "userid")
    private User user;

} 