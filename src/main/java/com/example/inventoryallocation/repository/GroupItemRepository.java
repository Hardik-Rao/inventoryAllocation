package com.example.inventoryallocation.repository;

import com.example.inventoryallocation.entity.GroupItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupItemRepository extends JpaRepository<GroupItem, Long> {
    List<GroupItem> findByComponentItemId(Long componentItemId);
}