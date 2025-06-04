package com.blm.takeout.repository;

import com.blm.takeout.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByShopId(Integer shopId);
    
    List<Item> findByCategoryId(Integer categoryId);
    
    @Query("SELECT i FROM Item i WHERE i.shopId = :shopId ORDER BY i.sales DESC")
    List<Item> findHotItemsByShopId(@Param("shopId") Integer shopId);
    
    @Query("SELECT i FROM Item i WHERE i.shopId = :shopId AND i.categoryId = :categoryId")
    List<Item> findByShopIdAndCategoryId(@Param("shopId") Integer shopId, @Param("categoryId") Integer categoryId);
} 