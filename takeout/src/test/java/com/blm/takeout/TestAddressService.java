package com.blm.takeout;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blm.takeout.entity.Address;
import com.blm.takeout.repository.AddressRepository;
import com.blm.takeout.service.GeoService;
import com.blm.takeout.service.impl.AddressServiceImpl;

public class TestAddressService {
    private AddressRepository addressRepository;
    private GeoService geoService;
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        geoService = mock(GeoService.class);
        addressService = new AddressServiceImpl();

        java.lang.reflect.Field repoField = null;
        java.lang.reflect.Field geoField = null;
        try {
            repoField = AddressServiceImpl.class.getDeclaredField("addressRepository");
            repoField.setAccessible(true);
            repoField.set(addressService, addressRepository);
            geoField = AddressServiceImpl.class.getDeclaredField("geoService");
            geoField.setAccessible(true);
            geoField.set(addressService, geoService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAddressesByUserId_success() {
        Address address = new Address();
        address.setUserId(1);
        when(addressRepository.findByUserId(1)).thenReturn(List.of(address));
        List<Address> result = addressService.getAddressesByUserId(1);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
    }

    @Test
    void getAddressesByUserId_notFound() {
        Address address = new Address();
        address.setUserId(1);
        when(addressRepository.findByUserId(2)).thenReturn(List.of());
        List<Address> result = addressService.getAddressesByUserId(2);
        assertEquals(0, result.size());
    }

    @Test
    void updateAddress_success() {
        Address address = new Address();
        address.setId(1);
        address.setUserId(1);
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(addressRepository.findByUserId(1)).thenReturn(List.of(address));
        when(geoService.getCoordinates("北京市海淀区学院路37号")).thenReturn(Map.of("latitude", 116.34085, "longitude", 39.97924));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        assertDoesNotThrow(() -> addressService.updateAddress(1, "奶龙", "13572344305", "北京市海淀区学院路37号", true));
        verify(addressRepository, atLeastOnce()).save(address);
        assertEquals("奶龙", address.getName());
        assertEquals("13572344305", address.getPhone());
        assertEquals("北京市海淀区学院路37号", address.getFullAddress());
        assertEquals(true, address.getCurrent());
        assertEquals(116.34085, address.getLatitude());
        assertEquals(39.97924, address.getLongitude());
    }

    @Test
    void updateAddress_notFound() {
        when(addressRepository.findById(2)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> addressService.updateAddress(2, "奶龙", "13572344305", "北京市海淀区学院路37号", true));
        assertEquals("地址不存在", ex.getMessage());
    }

    @Test
    void addAddress_success() {
        when(geoService.getCoordinates("北京")).thenReturn(Map.of("latitude", 1.0, "longitude", 2.0));
        when(addressRepository.findByUserId(1)).thenReturn(Collections.emptyList());
        when(addressRepository.save(any(Address.class))).thenReturn(new Address()); // 修正此行
        assertDoesNotThrow(() -> addressService.addAddress(1, "李四", "13900000000", "北京", true));
        verify(addressRepository, atLeastOnce()).save(any(Address.class));
    }

    @Test
    void addAddress_nullParam() {
        when(geoService.getCoordinates(null)).thenThrow(new IllegalArgumentException("地址不能为空"));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> addressService.addAddress(1, "李四", "13900000000", null, true));
        assertEquals("地址不能为空", ex.getMessage());
    }
}
