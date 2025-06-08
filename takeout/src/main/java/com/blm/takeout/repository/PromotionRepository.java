package com.blm.takeout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blm.takeout.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
}