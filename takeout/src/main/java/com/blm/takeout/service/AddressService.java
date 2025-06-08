package com.blm.takeout.service;

import com.blm.takeout.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUserId(Integer userId);
    void updateAddress(Integer id, String name, String phone, String fullAddress, Boolean current);
    void addAddress(Integer userId, String name, String phone, String fullAddress, Boolean current);
    void deleteAddress(Integer id);
    void setCurrentAddress(Integer id);
} 