package com.example.inventoryallocation.controller;

import com.example.inventoryallocation.entity.GroupItemSales;
import com.example.inventoryallocation.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/allocation")
public class AllocationController {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private com.example.inventoryallocation.repository.InventoryRepository inventoryRepository;


    @PostMapping("/run")
    public List<GroupItemSales> runAllocation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return allocationService.allocate(date);
    }


    @GetMapping("/inventory-totals")
    public List<java.util.Map<String, Object>> getTotalQuantityByComponent() {
        return inventoryRepository.getTotalQuantityForEachType();
    }
}