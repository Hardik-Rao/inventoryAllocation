package com.example.inventoryallocation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "group_items")
@Data
public class GroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupItemId;

    private String groupItemName;

    private Long componentItemId;

    private Integer quantityRequired;

    private Integer priority;

    private Integer demand;
}