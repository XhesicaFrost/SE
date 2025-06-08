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
import com.blm.takeout.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void registerSeller(String shopName, String shopAddress, String shopTags, MultipartFile shopImage, Integer userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        if (sellerRepository.findByUser_Userid(userId).isPresent()) {
            throw new BusinessException("您已经注册过店铺");
        }
        String imagePath = FileUtils.saveImage(shopImage);
        List<String> tags = objectMapper.readValue(shopTags, new TypeReference<List<String>>() {});
        Seller seller = new Seller();
        seller.setName(shopName);
        seller.setAddress(shopAddress);
        seller.setTags(tags);
        seller.setImage(imagePath);
        seller.setSellerStatus(Seller.Status.审批中);
        seller.setUser(user);
        sellerRepository.save(seller);
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
        sellerRepository.save(seller);
    }
    public Seller getSellerByUserId(Integer userId) {
        return sellerRepository.findByUser_Userid(userId).orElse(null);
    }
    public Seller getSellerById(Integer sellerId) {
        return sellerRepository.findById(sellerId).orElse(null);
    }
    
}
