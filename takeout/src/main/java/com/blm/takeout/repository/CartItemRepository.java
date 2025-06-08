package com.blm.takeout.repository;

import com.blm.takeout.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    CartItem findByUserIdAndItemId(Integer userId, Integer itemId);
    List<CartItem> findByUserId(Integer userId);
    void deleteByUserIdAndItemId(Integer userId, Integer itemId);
    void deleteByUserId(Integer userId);
} 