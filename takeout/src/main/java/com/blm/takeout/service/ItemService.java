package com.blm.takeout.service;

import com.blm.takeout.dto.ItemDTO;
import com.blm.takeout.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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
        item.setPrice(itemPrice);
        item.setDescription(itemDescription != null ? itemDescription : "");
        item.setStatus(Item.Status.审批中);
        itemRepository.save(item);
    }

    public Item getItemById(Integer id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Transactional
    public void editItem(Integer itemId, String itemName, Double itemPrice, MultipartFile itemImage, String itemDescription) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));

        item.setName(itemName);
        item.setPrice(itemPrice);

        if (itemImage != null && !itemImage.isEmpty()) {
            String imagePath = FileUtils.saveImage(itemImage);
            item.setImage(imagePath);
        }

        item.setDescription(itemDescription != null ? itemDescription : "");
        itemRepository.save(item);
    }

    @Transactional
    public void offlineItem(Integer itemId, Integer sellerId) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));
        if (!item.getShopId().equals(sellerId)) {
            throw new Exception("商家ID不匹配，无法下架商品");
        }
        item.setStatus(Item.Status.下架);
        itemRepository.save(item);
    }

    @Transactional
    public void onlineItem(Integer itemId, Integer sellerId) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new Exception("商品不存在"));
        if (!item.getShopId().equals(sellerId)) {
            throw new Exception("商家ID不匹配，无法上架商品");
        }
        item.setStatus(Item.Status.正常);
        itemRepository.save(item);
    }
    public List<Item> getNormalAndOffShelfItemsBySeller(Integer sellerId) {
        return itemRepository.findByShopIdAndStatusIn(sellerId, Arrays.asList(Item.Status.正常, Item.Status.下架));
    }
}
