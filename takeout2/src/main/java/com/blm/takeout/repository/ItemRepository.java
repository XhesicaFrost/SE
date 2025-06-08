package com.blm.takeout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blm.takeout.entity.Item;
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    
}