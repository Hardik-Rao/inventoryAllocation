package com.example.inventoryallocation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "group_item_sales")
@Data
public class GroupItemSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupItemId;

    private String groupItemName;

    private LocalDate saleDate;

    private Integer allocatedQuantity;
}