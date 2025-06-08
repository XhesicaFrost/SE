package com.blm.takeout.repository;

import com.blm.takeout.entity.UserShopPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface UserShopPreferenceRepository extends JpaRepository<UserShopPreference, Integer> {
    Optional<UserShopPreference> findByUserId(Integer userId);
} 