package com.blm.takeout;

import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.entity.*;
import com.blm.takeout.repository.*;
import com.blm.takeout.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCartService {

    private CartItemRepository cartItemRepository;
    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private PromotionRepository promotionRepository;
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartItemRepository = mock(CartItemRepository.class);
        itemRepository = mock(ItemRepository.class);
        shopRepository = mock(ShopRepository.class);
        promotionRepository = mock(PromotionRepository.class);
        cartService = new CartServiceImpl(cartItemRepository, itemRepository, shopRepository, promotionRepository);
    }

    @Test
    void updateCartItem_addNew_success() {
        when(cartItemRepository.findByUserIdAndItemId(1, 1)).thenReturn(Optional.empty());
        Item item = new Item();
        item.setId(1);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        CartItem saved = new CartItem();
        saved.setUserId(1);
        saved.setItem(item);
        saved.setQuantity(2);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(saved);

        CartItem result = cartService.updateCartItem(1, 1, 2);
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
    }

    @Test
    void updateCartItem_itemNotFound() {
        when(cartItemRepository.findByUserIdAndItemId(1, 1)).thenReturn(Optional.empty());
        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> cartService.updateCartItem(1, 1, 2));
        assertEquals("商品不存在", ex.getMessage());
    }

    @Test
    void updateCartItem_negativeQuantity() {
        when(cartItemRepository.findByUserIdAndItemId(1, 1)).thenReturn(Optional.empty());
        Item item = new Item();
        item.setId(1);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Exception ex = assertThrows(RuntimeException.class, () -> cartService.updateCartItem(1, 1, -1));
        assertEquals("商品数量不能为负数", ex.getMessage());
    }

    @Test
    void calculatePromotionPrice_withPromotion() {
        Promotion promo = new Promotion();
        promo.setStartTime(LocalDateTime.now().minusDays(1));
        promo.setEndTime(LocalDateTime.now().plusDays(1));
        promo.setFull(100.0);
        promo.setMinus(20.0);
        when(promotionRepository.findBySellerId(1)).thenReturn(List.of(promo));
        double price = cartService.calculatePromotionPrice(1, 120);
        assertEquals(100, price);
    }

    @Test
    void calculatePromotionPrice_noPromotion() {
        when(promotionRepository.findBySellerId(1)).thenReturn(Collections.emptyList());
        double price = cartService.calculatePromotionPrice(1, 120);
        assertEquals(120, price);
    }

    @Test
    void getCartItems_success() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        Item item = new Item();
        item.setId(1);
        item.setName("商品A");
        item.setPrice(10.0);
        item.setShopId(1);
        cartItem.setItem(item);
        when(cartItemRepository.findByUserId(1)).thenReturn(List.of(cartItem));

        Shop shop = new Shop();
        shop.setId(1);
        shop.setName("店铺A");
        shop.setAddress("地址A");
        shop.setImage("");
        when(shopRepository.findById(1)).thenReturn(Optional.of(shop));
        when(promotionRepository.findBySellerId(1)).thenReturn(Collections.emptyList());

        List<CartDTO> result = cartService.getCartItems(1);
        assertEquals(1, result.size());
        assertEquals("店铺A", result.get(0).getShop().get("name"));
    }

    @Test
    void getCartItems_shopNotFound() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        Item item = new Item();
        item.setId(1);
        item.setName("商品A");
        item.setPrice(10.0);
        item.setShopId(1);
        cartItem.setItem(item);
        when(cartItemRepository.findByUserId(1)).thenReturn(List.of(cartItem));
        when(shopRepository.findById(1)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> cartService.getCartItems(1));
        assertEquals("店铺不存在", ex.getMessage());
    }

    @Test
    void deleteCartItem_success() {
        doNothing().when(cartItemRepository).deleteByUserIdAndItemId(1, 1);
        assertDoesNotThrow(() -> cartService.deleteCartItem(1, 1));
        verify(cartItemRepository).deleteByUserIdAndItemId(1, 1);
    }

    @Test
    void clearCart_success() {
        CartItem cartItem = new CartItem();
        when(cartItemRepository.findByUserId(1)).thenReturn(List.of(cartItem));
        doNothing().when(cartItemRepository).delete(cartItem);
        assertDoesNotThrow(() -> cartService.clearCart(1));
        verify(cartItemRepository).delete(cartItem);
    }
}
