package com.blm.takeout.repository;

import com.blm.takeout.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByShopId(Integer shopId);
    
    @Query("SELECT i FROM Item i WHERE i.shopId = :shopId ORDER BY i.sales DESC")
    List<Item> findTop5ByShopIdOrderBySalesDesc(@Param("shopId") Integer shopId);
    
    @Query("SELECT i FROM Item i WHERE i.shopId = :shopId AND i.status = '正常'")
    List<Item> findActiveItemsByShopId(@Param("shopId") Integer shopId);
    
    Page<Item> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<Item> findByShopId(Integer shopId, Pageable pageable);
    List<Item> findByShopIdAndStatusIn(Integer shopId, List<Item.Status> statuses);
} 