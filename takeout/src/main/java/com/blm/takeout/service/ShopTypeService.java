package com.blm.takeout.service;

import com.blm.takeout.entity.ShopType;
import com.blm.takeout.repository.ShopTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShopTypeService {
    private final ShopTypeRepository shopTypeRepository;

    @Autowired
    public ShopTypeService(ShopTypeRepository shopTypeRepository) {
        this.shopTypeRepository = shopTypeRepository;
    }

    public List<ShopType> getAllShopTypes() {
        return shopTypeRepository.findAllByOrderByIdAsc();
    }
} 