package com.blm.takeout.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.blm.takeout.entity.Item;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    @Transactional
    public void registerItem(String itemName, MultipartFile itemImage, Double itemPrice, Integer sellerId, String itemDescription) throws Exception {
        String imagePath = FileUtils.saveImage(itemImage);
        Item item = new Item();
        item.setName(itemName);
        item.setImage(imagePath);
        item.setShopId(sellerId);
        item.setDescription(itemDescription != null ? itemDescription : "");
        item.setStatus(Item.Status.审批中);
        itemRepository.save(item);
    }

    public Item getItemById(Integer id) {
        return itemRepository.findById(id).orElse(null);
    }
}