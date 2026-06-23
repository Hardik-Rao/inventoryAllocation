package com.example.inventoryallocation.service;

import com.example.inventoryallocation.entity.GroupItem;
import com.example.inventoryallocation.entity.GroupItemSales;
import com.example.inventoryallocation.entity.Inventory;
import com.example.inventoryallocation.repository.GroupItemRepository;
import com.example.inventoryallocation.repository.GroupItemSalesRepository;
import com.example.inventoryallocation.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private GroupItemRepository groupItemRepository;

    @Autowired
    private GroupItemSalesRepository groupItemSalesRepository;

    public List<GroupItemSales> allocate(LocalDate date) {

        // Step 1: Get all inventory for this date (available stock per component)
        List<Inventory> inventoryList = inventoryRepository.findByInventoryDate(date);

        // Map: componentItemId -> available quantity
        Map<Long, Integer> availableStock = new HashMap<>();
        for (Inventory inv : inventoryList) {
            availableStock.put(inv.getComponentItemId(), inv.getAvailableQuantity());
        }

        // Step 2: Get all group items (kits) - we need ALL of them, grouped by component
        List<GroupItem> allGroupItems = groupItemRepository.findAll();

        // Map: componentItemId -> list of GroupItems that need this component
        Map<Long, List<GroupItem>> componentToGroupItems = allGroupItems.stream()
                .collect(Collectors.groupingBy(GroupItem::getComponentItemId));

        // Step 3: For each component, calculate how much each group item gets
        // Map: groupItemId -> achievable quantity for THIS component
        // We track the MIN across all components a group item needs (bottleneck logic)
        Map<Long, Integer> groupItemAchievableQty = new HashMap<>();
        Map<Long, String> groupItemNames = new HashMap<>();

        for (Map.Entry<Long, List<GroupItem>> entry : componentToGroupItems.entrySet()) {
            Long componentId = entry.getKey();
            List<GroupItem> competingItems = entry.getValue();

            int available = availableStock.getOrDefault(componentId, 0);

            // Calculate total demand for this component across all competing group items
            // demand here = quantityRequired * demand (units requested)
            Map<Long, Integer> rawDemand = new HashMap<>();
            int totalDemand = 0;
            for (GroupItem gi : competingItems) {
                int demandForThis = gi.getQuantityRequired() * gi.getDemand();
                rawDemand.put(gi.getGroupItemId(), demandForThis);
                totalDemand += demandForThis;
                groupItemNames.put(gi.getGroupItemId(), gi.getGroupItemName());
            }

            Map<Long, Integer> allocatedForComponent;

            if (totalDemand <= available) {
                // No shortage - everyone gets full demand
                allocatedForComponent = rawDemand;
            } else {
                // Shortage - ratio-based proportional split + priority tiebreaker for remainder
                allocatedForComponent = proportionalAllocate(competingItems, rawDemand, totalDemand, available);
            }

            // Now convert "component quantity allocated" back into "how many KIT units that supports"
            // kitUnitsSupported = allocatedQtyForComponent / quantityRequired per kit
            for (GroupItem gi : competingItems) {
                int allocatedComponentQty = allocatedForComponent.getOrDefault(gi.getGroupItemId(), 0);
                int kitUnitsSupported = gi.getQuantityRequired() == 0 ? 0 : allocatedComponentQty / gi.getQuantityRequired();

                // Bottleneck logic: take the MIN across all components this group item needs
                groupItemAchievableQty.merge(gi.getGroupItemId(), kitUnitsSupported, Math::min);
            }
        }

        // Step 4: Save results into GroupItemSales
        List<GroupItemSales> results = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : groupItemAchievableQty.entrySet()) {
            GroupItemSales sale = new GroupItemSales();
            sale.setGroupItemId(entry.getKey());
            sale.setGroupItemName(groupItemNames.get(entry.getKey()));
            sale.setSaleDate(date);
            sale.setAllocatedQuantity(entry.getValue());
            results.add(groupItemSalesRepository.save(sale));
        }

        return results;
    }

    /**
     * Ratio-based proportional allocation with priority as tiebreaker for remainder units.
     */
    private Map<Long, Integer> proportionalAllocate(
            List<GroupItem> competingItems,
            Map<Long, Integer> rawDemand,
            int totalDemand,
            int available) {

        Map<Long, Integer> floorAllocation = new HashMap<>();
        Map<Long, Double> remainders = new HashMap<>();
        int allocatedSum = 0;

        // Step A: floor division for everyone
        for (GroupItem gi : competingItems) {
            Long id = gi.getGroupItemId();
            double exactShare = ((double) rawDemand.get(id) / totalDemand) * available;
            int floorShare = (int) Math.floor(exactShare);
            floorAllocation.put(id, floorShare);
            remainders.put(id, exactShare - floorShare);
            allocatedSum += floorShare;
        }

        // Step B: distribute leftover remainder units
        int leftover = available - allocatedSum;

        // Sort by: remainder descending, then priority ascending (lower number = higher priority) as tiebreaker
        List<GroupItem> sortedForRemainder = new ArrayList<>(competingItems);
        sortedForRemainder.sort((a, b) -> {
            int remainderCompare = Double.compare(remainders.get(b.getGroupItemId()), remainders.get(a.getGroupItemId()));
            if (remainderCompare != 0) return remainderCompare;
            return Integer.compare(a.getPriority(), b.getPriority()); // priority as tiebreaker
        });

        for (int i = 0; i < leftover && i < sortedForRemainder.size(); i++) {
            Long id = sortedForRemainder.get(i).getGroupItemId();
            floorAllocation.merge(id, 1, Integer::sum);
        }

        return floorAllocation;
    }
}
