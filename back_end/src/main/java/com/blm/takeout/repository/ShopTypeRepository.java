package com.blm.takeout.repository;

import com.blm.takeout.entity.ShopType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShopTypeRepository extends JpaRepository<ShopType, Integer> {
    List<ShopType> findAllByOrderByIdAsc();
} 