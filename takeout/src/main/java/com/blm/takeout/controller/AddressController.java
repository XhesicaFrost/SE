package com.blm.takeout.controller;

import com.blm.takeout.entity.Address;
import com.blm.takeout.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public List<Address> getAddresses(@RequestParam Integer userId) {
        return addressService.getAddressesByUserId(userId);
    }

    @PostMapping("/edit")
    public ResponseEntity<Map<String, String>> editAddress(@RequestParam(required = false) Integer id,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String phone,
                                                         @RequestParam(required = false) String fullAddress,
                                                         @RequestParam(required = false) Boolean current) {
        try {
            addressService.updateAddress(id, name, phone, fullAddress, current);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "fail"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addAddress(@RequestParam Integer userId,
                                                        @RequestParam String name,
                                                        @RequestParam String phone,
                                                        @RequestParam String fullAddress,
                                                        @RequestParam(required = false) Boolean current) {
        try {
            addressService.addAddress(userId, name, phone, fullAddress, current);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "fail"));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Boolean>> deleteAddress(@RequestBody Map<String, String> request) {
        try {
            addressService.deleteAddress(Integer.parseInt(request.get("id")));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false));
        }
    }

    @PostMapping("/current")
    public ResponseEntity<Map<String, Boolean>> setCurrentAddress(@RequestBody Map<String, String> request) {
        try {
            addressService.setCurrentAddress(Integer.parseInt(request.get("id")));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false));
        }
    }
} 