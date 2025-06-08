package com.blm.takeout.repository;

import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_Userid(Integer userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUser_UseridOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.user.userid = :userId")
    List<Order> findByUserId(@Param("userId") Integer userId);
    @Query("SELECT o FROM Order o WHERE o.user.userid = :userId")
    Page<Order> findByUserId(@Param("userId") Integer userId, Pageable pageable);
    List<Order> findByShopId(Integer shopId);
    @Query("SELECT o FROM Order o WHERE o.user.userid = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
    
    Page<Order> findByRiderId(Integer riderId, Pageable pageable);
    
    List<Order> findByRiderIdAndStatusIn(Integer riderId, List<OrderStatus> statuses);
    
    long countByRiderIdAndStatusIn(Integer riderId, List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE o.riderId = :riderId AND o.status IN :statuses")
    List<Order> findActiveOrdersByRider(@Param("riderId") Integer riderId, @Param("statuses") List<OrderStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Integer id);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.riderId = :riderId " +
           "AND o.status = :status " +
           "AND o.completedTime BETWEEN :startTime AND :endTime")
    long countByRiderIdAndStatusAndCompletedTimeBetween(
        @Param("riderId") Integer riderId,
        @Param("status") OrderStatus status,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    List<Order> findByRiderIdAndStatus(Integer riderId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.riderId IS NULL")
    List<Order> findByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status " +
           "AND o.riderId IS NULL " +
           "AND (:merchantName IS NULL OR o.shop.name LIKE %:merchantName%) " +
           "AND (:userAddress IS NULL OR o.deliveryAddress LIKE %:userAddress%)")
    List<Order> findOrdersByFilters(
        @Param("status") OrderStatus status,
        @Param("merchantName") String merchantName,
        @Param("userAddress") String userAddress
    );
} 