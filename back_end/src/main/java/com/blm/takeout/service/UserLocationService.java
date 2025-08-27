package com.blm.takeout.service;

import com.blm.takeout.entity.UserLocation;
import com.blm.takeout.repository.UserLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserLocationService {
    private final UserLocationRepository userLocationRepository;

    @Autowired
    public UserLocationService(UserLocationRepository userLocationRepository) {
        this.userLocationRepository = userLocationRepository;
    }

    public Optional<UserLocation> getUserLocation(Integer userId) {
        return userLocationRepository.findByUserId(userId);
    }

    @Transactional
    public UserLocation updateUserLocation(Integer userId, Double latitude, Double longitude, String address) {
        UserLocation location = userLocationRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserLocation newLocation = new UserLocation();
                newLocation.setUserId(userId);
                newLocation.setCreatedAt(LocalDateTime.now());
                return newLocation;
            });

        location.setLatitude(latitude);
        location.setLongitude(longitude);
        if (address != null) {
            location.setAddress(address);
        }
        location.setUpdatedAt(LocalDateTime.now());

        return userLocationRepository.save(location);
    }
} 