package com.blm.takeout.service;

import com.blm.takeout.dto.ItemDTO;
import com.blm.takeout.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDTO> getItemsByShopId(Integer shopId);
    Item getItemById(Integer id);
    Page<Item> searchItems(String keyword, PageRequest pageRequest);
    Page<Item> getItemsByShopId(Integer shopId, Pageable pageable);
    Map<String, Object> getItemDetails(Integer itemId);
    void registerItem(String itemName, MultipartFile itemImage, Double itemPrice, Integer sellerId, String itemDescription) throws Exception;
    void editItem(Integer itemId, String itemName, Double itemPrice, MultipartFile itemImage, String itemDescription) throws Exception;
    List<Item> getNormalAndOffShelfItemsBySeller(Integer sellerId);
}
