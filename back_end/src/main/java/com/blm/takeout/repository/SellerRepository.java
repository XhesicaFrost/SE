package com.blm.takeout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blm.takeout.entity.Seller;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Optional<Seller> findByUser_Userid(Integer userId);
}
