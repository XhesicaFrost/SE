package com.blm.takeout.service.impl;

import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.ItemReview;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.repository.ItemReviewRepository;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final ItemReviewRepository itemReviewRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository,
            ShopRepository shopRepository,
            ItemReviewRepository itemReviewRepository) {
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
        this.itemReviewRepository = itemReviewRepository;
    }

    @Override
    public Item getItemById(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public Page<Item> searchItems(String keyword, PageRequest pageRequest) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return itemRepository.findAll(pageRequest);
        }
        return itemRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageRequest);
    }

    @Override
    public Page<Item> getItemsByShopId(Integer shopId, Pageable pageable) {
        return itemRepository.findByShopId(shopId, pageable);
    }

    @Override
    public Map<String, Object> getItemDetails(Integer itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        Shop shop = shopRepository.findById(item.getShopId())
            .orElseThrow(() -> new RuntimeException("店铺不存在"));

        List<ItemReview> reviews = itemReviewRepository.findByItemIdOrderByCreatedAtDesc(itemId);

        Map<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("id", item.getId());
        itemDetails.put("name", item.getName());
        itemDetails.put("price", item.getPrice());
        itemDetails.put("image", item.getImage());
        itemDetails.put("description", item.getDescription());
        itemDetails.put("sales", item.getSales());
        itemDetails.put("rating", item.getRating());
        itemDetails.put("shopId", item.getShopId());
        itemDetails.put("shopName", shop.getName());

        itemDetails.put("reviews", reviews.stream()
            .map(review -> Map.of(
                "id", review.getId(),
                "userId", review.getUserId(),
                "username", "用户" + review.getUserId(),
                "rating", review.getRating(),
                "content", review.getContent(),
                "createTime", review.getCreatedAt(),
                "images", review.getImages()
            ))
            .collect(Collectors.toList()));

        return itemDetails;
    }

    @Override
    public List<Item> getItemsByShopId(Integer shopId) {
        return itemRepository.findByShopId(shopId);
    }

    @Override
    @Transactional
    public void registerItem(String itemName, MultipartFile itemImage, Double itemPrice, Integer sellerId, String itemDescription) throws Exception {
        // 直接使用sellerId作为shopId
        Shop shop = shopRepository.findById(sellerId)
            .orElseThrow(() -> new Exception("未找到对应的店铺信息"));
        
        String imagePath = FileUtils.saveImage(itemImage);
        Item item = new Item();
        item.setName(itemName);
        item.setImage(imagePath);
        item.setShopId(shop.getId());
        item.setPrice(itemPrice);
        item.setDescription(itemDescription != null ? itemDescription : "");
        item.setStatus(Item.Status.审批中);
        item.setRating(0.0);
        item.setSales(0);
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void editItem(Integer itemId, String itemName, String itemDescription, Double itemPrice, MultipartFile itemImage) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));

        item.setName(itemName);
        item.setPrice(itemPrice);
        item.setDescription(itemDescription != null ? itemDescription : "");

        if (itemImage != null && !itemImage.isEmpty()) {
            String imagePath = FileUtils.saveImage(itemImage);
            item.setImage(imagePath);
        }

        item.setStatus(Item.Status.审批中);  // 设置状态为审批中
        itemRepository.save(item);
    }

    @Override
    public List<Item> getNormalAndOffShelfItemsByShop(Integer shopId) {
        return itemRepository.findByShopIdAndStatusIn(shopId, 
            List.of(Item.Status.正常, Item.Status.下架));
    }

    @Override
    @Transactional
    public void offlineItem(Integer itemId, Integer shopId) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));
        if (!item.getShopId().equals(shopId)) {
            throw new Exception("商家ID不匹配，无法下架商品");
        }
        item.setStatus(Item.Status.下架);
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void onlineItem(Integer itemId, Integer shopId) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));
        if (!item.getShopId().equals(shopId)) {
            throw new Exception("商家ID不匹配，无法上架商品");
        }
        item.setStatus(Item.Status.正常);
        itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
} 