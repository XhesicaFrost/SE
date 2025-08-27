package com.blm.takeout.service.impl;

import com.blm.takeout.entity.Seller;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.SellerRepository;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopImportService {

    private final SellerRepository sellerRepository;
    private final ShopRepository shopRepository;

    @Autowired
    public ShopImportService(SellerRepository sellerRepository, ShopRepository shopRepository) {
        this.sellerRepository = sellerRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional
    public void importSellersToShops() {
        List<Seller> sellers = sellerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Seller seller : sellers) {
            // 检查是否已经存在对应的shop
            if (shopRepository.findByUserId(seller.getUser().getUserid()) == null) {
                Shop shop = new Shop();
                shop.setName(seller.getName());
                shop.setAddress(seller.getAddress());
                shop.setImage(seller.getImage());
                shop.setBusinessHours("09:00-22:00"); // 默认营业时间
                shop.setDeliveryFee(5.0); // 默认配送费
                shop.setMinPrice(20.0); // 默认最低消费
                shop.setMaxPrice(100.0); // 默认最高消费
                shop.setType("餐饮"); // 默认类型
                shop.setPhone("13800138000"); // 默认电话
                shop.setRating(5.0); // 默认评分
                shop.setSales(0); // 初始销量
                shop.setIsOpen(true); // 默认营业状态
                shop.setLatitude(0.0); // 默认纬度
                shop.setLongitude(0.0); // 默认经度
                shop.setCreatedAt(now);
                shop.setUpdatedAt(now);

                shopRepository.save(shop);
            }
        }
    }
} 