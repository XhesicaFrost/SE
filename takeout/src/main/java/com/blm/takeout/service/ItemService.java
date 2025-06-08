package com.blm.takeout.service;

import com.blm.takeout.dto.ItemDTO;
import com.blm.takeout.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDTO> getItemsByShopId(Integer shopId);
    Item getItemById(Integer id);
    Page<Item> searchItems(String keyword, Pageable pageable);
    Page<Item> getItemsByShopId(Integer shopId, Pageable pageable);
    Map<String, Object> getItemDetails(Integer itemId);
} 