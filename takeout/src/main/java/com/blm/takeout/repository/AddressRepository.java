package com.blm.takeout.repository;

import com.blm.takeout.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer userId);
    Address findByUserIdAndCurrentTrue(Integer userId);
} 