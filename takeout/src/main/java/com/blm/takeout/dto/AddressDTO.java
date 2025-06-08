package com.blm.takeout.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Integer id;
    private String name;
    private String phone;
    private String fullAddress;
    private Boolean current;
} 