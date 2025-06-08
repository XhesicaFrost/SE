package com.blm.takeout.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.blm.takeout.entity.*;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.repository.SellerRepository;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void registerSeller(String shopName, String shopAddress, String shopTags, MultipartFile shopImage, Integer userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        if (sellerRepository.findByUser_Userid(userId).isPresent()) {
            throw new BusinessException("您已经注册过店铺");
        }
        String imagePath = FileUtils.saveImage(shopImage);
        List<String> tags = objectMapper.readValue(shopTags, new TypeReference<List<String>>() {});
        
        // 创建商家记录
        Seller seller = new Seller();
        seller.setName(shopName);
        seller.setAddress(shopAddress);
        seller.setTags(tags);
        seller.setImage(imagePath);
        seller.setSellerStatus(Seller.Status.审批中);
        seller.setUser(user);
        sellerRepository.save(seller);

        // 创建店铺记录
        Shop shop = new Shop();
        shop.setName(shopName);
        shop.setAddress(shopAddress);
        shop.setImage(imagePath);
        shop.setUserId(userId);
        shop.setStatus(Shop.Status.审批中);  // 设置与seller相同的状态
        shop.setBusinessHours("09:00-22:00");  // 默认营业时间
        shop.setDeliveryFee(5.0);  // 默认配送费
        shop.setMinPrice(20.0);    // 默认起送价
        shop.setMaxPrice(100.0);   // 默认最高价
        shop.setType("其他");      // 默认店铺类型
        shop.setPhone(user.getPhonenumber());  // 使用用户手机号
        shop.setRating(5.0);       // 初始评分
        shop.setSales(0);          // 初始销量
        shop.setIsOpen(true);      // 默认营业状态
        shop.setLatitude(0.0);     // 默认纬度
        shop.setLongitude(0.0);    // 默认经度
        shopRepository.save(shop);
    }

    @Transactional
    public void editSellerInfo(Integer sellerId, String shopName, String shopAddress, MultipartFile shopImage, String shopTags) throws Exception {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new Exception("商家不存在"));
        seller.setName(shopName);
        seller.setAddress(shopAddress);
        if (shopImage != null && !shopImage.isEmpty()) {
            String imagePath = FileUtils.saveImage(shopImage);
            seller.setImage(imagePath);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> tags = objectMapper.readValue(shopTags, new TypeReference<List<String>>() {});
        seller.setTags(tags);
        seller.setSellerStatus(Seller.Status.审批中);  // 设置状态为审批中
        sellerRepository.save(seller);

        // 同步更新shop的状态
        Shop shop = shopRepository.findByUserId(seller.getUser().getUserid());
        if (shop != null) {
            shop.setStatus(Shop.Status.审批中);
            shopRepository.save(shop);
        }
    }
    public Seller getSellerByUserId(Integer userId) {
        return sellerRepository.findByUser_Userid(userId).orElse(null);
    }
    public Seller getSellerById(Integer sellerId) {
        return sellerRepository.findById(sellerId).orElse(null);
    }
    
}
