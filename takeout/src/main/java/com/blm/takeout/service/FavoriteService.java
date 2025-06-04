package com.blm.takeout.service;

import com.blm.takeout.dto.FavoriteDTO;
import com.blm.takeout.entity.Favorite;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.FavoriteRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                          ShopRepository shopRepository,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.shopRepository = shopRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addFavorite(Integer userId, Favorite.TargetType targetType, Integer targetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (favoriteRepository.existsByUser_useridAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new RuntimeException("Already favorited");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }

    public Page<FavoriteDTO> getUserFavorites(Integer userId, Pageable pageable) {
        Page<Favorite> favorites = favoriteRepository.findByUser_useridOrderByCreatedAtDesc(userId, pageable);
        return favorites.map(this::convertToDTO);
    }

    public Page<FavoriteDTO> getUserFavoritesByType(Integer userId, 
            Favorite.TargetType targetType, Pageable pageable) {
        Page<Favorite> favorites = favoriteRepository
                .findByUserIdAndTargetTypeOrderByCreatedAtDesc(userId, targetType, pageable);
        return favorites.map(this::convertToDTO);
    }

    @Transactional
    public void removeFavorite(Integer userId, Favorite.TargetType targetType, Integer targetId) {
        favoriteRepository.deleteByUser_useridAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    private FavoriteDTO convertToDTO(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setUserId(favorite.getUser().getUserid());
        dto.setTargetType(favorite.getTargetType());
        dto.setTargetId(favorite.getTargetId());
        dto.setCreatedAt(favorite.getCreatedAt());

        if (favorite.getTargetType() == Favorite.TargetType.SHOP) {
            Shop shop = shopRepository.findById(favorite.getTargetId())
                    .orElse(null);
            if (shop != null) {
                dto.setName(shop.getName());
                dto.setImage(shop.getImage());
                dto.setDescription(shop.getDescription());
            }
        } else {
            Item item = itemRepository.findById(favorite.getTargetId())
                    .orElse(null);
            if (item != null) {
                dto.setName(item.getName());
                dto.setImage(item.getImage());
                dto.setDescription(item.getDescription());
            }
        }

        return dto;
    }
} 