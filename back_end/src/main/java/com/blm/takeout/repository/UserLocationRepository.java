package com.blm.takeout.repository;

import com.blm.takeout.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Integer> {
    Optional<UserLocation> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
} 