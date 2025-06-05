package com.blm.takeout.service;

import com.blm.takeout.dto.FavoriteDTO;
import com.blm.takeout.entity.Favorite.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    void addFavorite(Integer userId, TargetType targetType, Integer targetId, FavoriteDTO favoriteDTO);
    Page<FavoriteDTO> getUserFavorites(Integer userId, Pageable pageable);
    Page<FavoriteDTO> getUserFavoritesByType(Integer userId, TargetType targetType, Pageable pageable);
    void removeFavorite(Integer userId, TargetType targetType, Integer targetId);
} 