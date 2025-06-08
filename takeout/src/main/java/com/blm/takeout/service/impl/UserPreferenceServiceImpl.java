package com.blm.takeout.service.impl;

import com.blm.takeout.dto.UserPreferenceDTO;
import com.blm.takeout.entity.UserPreference;
import com.blm.takeout.repository.UserPreferenceRepository;
import com.blm.takeout.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Override
    public UserPreferenceDTO getUserPreference(String userId) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId);
        if (preference == null) {
            return null;
        }

        UserPreferenceDTO dto = new UserPreferenceDTO();
        dto.setCategories(preference.getCategories());

        if (preference.getPriceRange() != null) {
            UserPreferenceDTO.PriceRange priceRange = new UserPreferenceDTO.PriceRange();
            priceRange.setMin(preference.getPriceRange().getMin());
            priceRange.setMax(preference.getPriceRange().getMax());
            dto.setPriceRange(priceRange);
        }

        if (preference.getLocation() != null) {
            UserPreferenceDTO.Location location = new UserPreferenceDTO.Location();
            location.setLatitude(preference.getLocation().getLatitude());
            location.setLongitude(preference.getLocation().getLongitude());
            location.setRadius(preference.getLocation().getRadius());
            dto.setLocation(location);
        }

        return dto;
    }

    @Override
    @Transactional
    public void updateUserPreference(String userId, UserPreferenceDTO preferenceDTO) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId);
        if (preference == null) {
            preference = new UserPreference();
            preference.setUserId(userId);
        }

        if (preferenceDTO.getCategories() != null) {
            preference.setCategories(preferenceDTO.getCategories());
        }

        if (preferenceDTO.getPriceRange() != null) {
            UserPreference.PriceRange priceRange = new UserPreference.PriceRange();
            priceRange.setMin(preferenceDTO.getPriceRange().getMin());
            priceRange.setMax(preferenceDTO.getPriceRange().getMax());
            preference.setPriceRange(priceRange);
        }

        if (preferenceDTO.getLocation() != null) {
            UserPreference.Location location = new UserPreference.Location();
            location.setLatitude(preferenceDTO.getLocation().getLatitude());
            location.setLongitude(preferenceDTO.getLocation().getLongitude());
            location.setRadius(preferenceDTO.getLocation().getRadius());
            preference.setLocation(location);
        }

        userPreferenceRepository.save(preference);
    }
} 