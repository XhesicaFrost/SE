package com.blm.takeout.service.impl;

import com.blm.takeout.dto.FavoriteDTO;
import com.blm.takeout.entity.Favorite;
import com.blm.takeout.entity.Favorite.TargetType;
import com.blm.takeout.repository.FavoriteRepository;
import com.blm.takeout.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public void addFavorite(Integer userId, TargetType targetType, Integer targetId, FavoriteDTO favoriteDTO) {
        if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favorite.setName(favoriteDTO.getName());
        favorite.setImage(favoriteDTO.getImage());
        favorite.setDescription(favoriteDTO.getDescription());
        favorite.setRating(favoriteDTO.getRating());

        favoriteRepository.save(favorite);
    }

    @Override
    public Page<FavoriteDTO> getUserFavorites(Integer userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<FavoriteDTO> getUserFavoritesByType(Integer userId, TargetType targetType, Pageable pageable) {
        return favoriteRepository.findByUserIdAndTargetTypeOrderByCreatedAtDesc(userId, targetType, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void removeFavorite(Integer userId, TargetType targetType, Integer targetId) {
        favoriteRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    private FavoriteDTO convertToDTO(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId().intValue());
        dto.setUserId(favorite.getUserId());
        dto.setTargetType(favorite.getTargetType());
        dto.setTargetId(favorite.getTargetId());
        dto.setCreatedAt(favorite.getCreatedAt());
        dto.setName(favorite.getName());
        dto.setImage(favorite.getImage());
        dto.setDescription(favorite.getDescription());
        dto.setRating(favorite.getRating());
        return dto;
    }
} 