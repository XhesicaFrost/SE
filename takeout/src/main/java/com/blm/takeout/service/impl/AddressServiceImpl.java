package com.blm.takeout.service.impl;

import com.blm.takeout.entity.Address;
import com.blm.takeout.repository.AddressRepository;
import com.blm.takeout.service.AddressService;
import com.blm.takeout.service.GeoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressRepository addressRepository;

    private GeoService geoService;

    @Override
    public List<Address> getAddressesByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void updateAddress(Integer id, String name, String phone, String fullAddress, Boolean current) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        if (name != null) address.setName(name);
        if (phone != null) address.setPhone(phone);
        if (fullAddress != null) address.setFullAddress(fullAddress);
        if (current != null && current) {
            // 将其他地址设置为非当前地址
            addressRepository.findByUserId(address.getUserId()).forEach(a -> {
                a.setCurrent(false);
                addressRepository.save(a);
            });
            address.setCurrent(true);
        }
        
        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void addAddress(Integer userId, String name, String phone, String fullAddress, Boolean current) {
        Map<String, Double> coordinates = geoService.getCoordinates(fullAddress);
        Double latitude = coordinates.get("latitude");
        Double longitude = coordinates.get("longitude");

        Address address = new Address();
        address.setUserId(userId);
        address.setName(name);
        address.setPhone(phone);
        address.setFullAddress(fullAddress);
        address.setCurrent(current != null && current);
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        
        if (current != null && current) {
            // 将其他地址设置为非当前地址
            addressRepository.findByUserId(userId).forEach(a -> {
                a.setCurrent(false);
                addressRepository.save(a);
            });
        }
        
        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void setCurrentAddress(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        // 将其他地址设置为非当前地址
        addressRepository.findByUserId(address.getUserId()).forEach(a -> {
            a.setCurrent(false);
            addressRepository.save(a);
        });
        
        address.setCurrent(true);
        addressRepository.save(address);
    }
} 