package com.blm.takeout.repository;

import com.blm.takeout.entity.Order.OrderStatus;
import com.blm.takeout.entity.Rider;
import com.blm.takeout.entity.Rider.RiderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Integer> {
    Optional<Rider> findByPhone(String phone);
    
    @Query("SELECT r FROM Rider r WHERE r.status = :status")
    List<Rider> findByStatus(@Param("status") RiderStatus status);
    
    @Query("SELECT r FROM Rider r WHERE r.status = 'ONLINE' AND r.isSmartDispatch = true")
    List<Rider> findAvailableRidersForSmartDispatch();
    
    @Query("SELECT r FROM Rider r WHERE " +
           "r.status = 'ONLINE' AND " +
           "ST_Distance_Sphere(point(r.currentLongitude, r.currentLatitude), " +
           "point(:shopLongitude, :shopLatitude)) <= :maxDistance")
    List<Rider> findNearbyRiders(@Param("shopLatitude") Double shopLatitude,
                                @Param("shopLongitude") Double shopLongitude,
                                @Param("maxDistance") Double maxDistance);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.riderId = :riderId AND o.status IN :statuses")
    long countByRiderIdAndStatusIn(@Param("riderId") Integer riderId, @Param("statuses") List<OrderStatus> statuses);
} 