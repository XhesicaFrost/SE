package com.blm.takeout.repository;

import com.blm.takeout.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
    Optional<Order> findByOrderNumber(String orderNumber);
} 