package com.blm.takeout.repository;

import com.blm.takeout.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(Integer userId);
    
    Optional<CartItem> findByUserIdAndItemId(Integer userId, Integer itemId);
    
    void deleteByUserIdAndItemId(Integer userId, Integer itemId);
    
    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId AND c.item.shop.id = :shopId")
    List<CartItem> findByUserIdAndShopId(@Param("userId") Integer userId, @Param("shopId") Integer shopId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.item.shop.id = :shopId AND c.selected = true")
    void deleteByUserIdAndItemShopIdAndSelectedTrue(@Param("userId") Integer userId, @Param("shopId") Integer shopId);
} 