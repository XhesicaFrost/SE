package com.blm.takeout.service;

import com.blm.takeout.dto.UserPreferenceDTO;

public interface UserPreferenceService {
    UserPreferenceDTO getUserPreference(String userId);
    void updateUserPreference(String userId, UserPreferenceDTO preferenceDTO);
} 