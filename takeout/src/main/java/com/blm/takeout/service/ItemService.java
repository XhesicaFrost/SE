package com.blm.takeout.service;

import com.blm.takeout.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import com.blm.takeout.util.FileUtils;

public interface ItemService {
    Item getItemById(Integer id);
    Page<Item> searchItems(String keyword, PageRequest pageRequest);
    Page<Item> getItemsByShopId(Integer shopId, Pageable pageable);
    Map<String, Object> getItemDetails(Integer itemId);
    List<Item> getItemsByShopId(Integer shopId);
    void registerItem(String itemName, MultipartFile itemImage, Double itemPrice, Integer sellerId, String itemDescription) throws Exception;
    @Transactional
    void editItem(Integer itemId, String itemName, String itemDescription, Double itemPrice, MultipartFile itemImage) throws Exception;
    void offlineItem(Integer itemId, Integer shopId) throws Exception;
    void onlineItem(Integer itemId, Integer shopId) throws Exception;
    List<Item> getNormalAndOffShelfItemsByShop(Integer shopId);
} 