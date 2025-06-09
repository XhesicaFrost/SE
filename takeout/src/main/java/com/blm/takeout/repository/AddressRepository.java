package com.blm.takeout.repository;

import com.blm.takeout.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByUserIdAndCurrentTrue(Integer userId);
    List<Address> findByUserId(Integer userId);
} 