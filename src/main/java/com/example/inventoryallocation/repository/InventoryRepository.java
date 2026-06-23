package com.example.inventoryallocation.repository;

import com.example.inventoryallocation.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByInventoryDate(LocalDate date);

    @Query("SELECT i.componentName as name, SUM(i.availableQuantity) as total FROM Inventory i GROUP BY i.componentName")
    List<Map<String, Object>> getTotalQuantityForEachType();
}