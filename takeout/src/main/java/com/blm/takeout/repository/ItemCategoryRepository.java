package com.blm.takeout.repository;

import com.blm.takeout.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Integer> {
    List<ItemCategory> findByShopIdOrderBySortOrderAsc(Integer shopId);
} 