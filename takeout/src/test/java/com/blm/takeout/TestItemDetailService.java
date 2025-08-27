package com.blm.takeout;

import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.ItemReview;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.Item.Status;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ItemReviewRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.service.ItemDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestItemDetailService {

    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private ItemReviewRepository reviewRepository;
    private ObjectMapper objectMapper;
    private ItemDetailService itemDetailService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        shopRepository = mock(ShopRepository.class);
        reviewRepository = mock(ItemReviewRepository.class);
        objectMapper = new ObjectMapper();
        itemDetailService = new ItemDetailService();
        inject(itemDetailService, "itemRepository", itemRepository);
        inject(itemDetailService, "shopRepository", shopRepository);
        inject(itemDetailService, "reviewRepository", reviewRepository);
        inject(itemDetailService, "objectMapper", objectMapper);
    }

    private void inject(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getItemDetail_success() {
        Item item = new Item();
        item.setId(1);
        item.setName("商品A");
        item.setDescription("描述A");
        item.setPrice(10.0);
        item.setImage("img.png");
        item.setSales(100);
        item.setRating(4.5);
        item.setStatus(Status.下架);
        item.setShopId(2);

        Shop shop = new Shop();
        shop.setId(2);
        shop.setName("店铺B");
        shop.setImage("shop.png");
        shop.setRating(4.8);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(shopRepository.findById(2)).thenReturn(Optional.of(shop));

        Map<String, Object> result = itemDetailService.getItemDetail(1);
        assertEquals("商品A", result.get("name"));
        assertEquals("店铺B", ((Map<?, ?>)result.get("shop")).get("name"));
    }

    @Test
    void getItemDetail_itemNotFound() {
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> itemDetailService.getItemDetail(1));
        assertEquals("商品不存在", ex.getMessage());
    }

    @Test
    void getItemDetail_shopNotFound() {
        Item item = new Item();
        item.setId(1);
        item.setShopId(2);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(shopRepository.findById(2)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> itemDetailService.getItemDetail(1));
        assertEquals("店铺不存在", ex.getMessage());
    }

    @Test
    void getItemReviews_success() throws Exception {
        ItemReview review = new ItemReview();
        review.setId(1);
        review.setUserId(10);
        review.setRating(5);
        review.setContent("好评");
        review.setImages(objectMapper.writeValueAsString(List.of("img1.png", "img2.png")));
        review.setCreatedAt(LocalDateTime.now());

        Page<ItemReview> page = new PageImpl<>(List.of(review), PageRequest.of(0, 2), 1);
        when(reviewRepository.findByItemIdOrderByCreatedAtDesc(1, PageRequest.of(0, 2))).thenReturn(page);

        Map<String, Object> result = itemDetailService.getItemReviews(1, 1, 2);
        assertEquals(1L, result.get("total"));
        assertEquals(1, ((List<?>)result.get("reviews")).size());
        assertEquals("好评", ((Map<?, ?>)((List<?>)result.get("reviews")).get(0)).get("content"));
        assertEquals(List.of("img1.png", "img2.png"), ((Map<?, ?>)((List<?>)result.get("reviews")).get(0)).get("images"));
    }

    @Test
    void getItemReviews_empty() {
        Page<ItemReview> page = new PageImpl<>(List.of(), PageRequest.of(0, 2), 0);
        when(reviewRepository.findByItemIdOrderByCreatedAtDesc(1, PageRequest.of(0, 2))).thenReturn(page);

        Map<String, Object> result = itemDetailService.getItemReviews(1, 1, 2);
        assertEquals(0L, result.get("total"));
        assertTrue(((List<?>)result.get("reviews")).isEmpty());
    }
}
