package com.blm.takeout.repository;

import com.blm.takeout.entity.ItemReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemReviewRepository extends JpaRepository<ItemReview, Integer> {
    List<ItemReview> findByItemIdOrderByCreatedAtDesc(Integer itemId);
    Page<ItemReview> findByItemIdOrderByCreatedAtDesc(Integer itemId, Pageable pageable);
} 