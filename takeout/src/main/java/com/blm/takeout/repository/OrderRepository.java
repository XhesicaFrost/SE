package com.blm.takeout.repository;

import com.blm.takeout.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderNumber(String orderNumber);
    Page<Order> findByUser_useridOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Order> findByUser_useridOrderByCreatedAtDesc(Integer userId);
} 