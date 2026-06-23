package com.example.inventoryallocation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long componentItemId;

    private String componentName;

    private LocalDate inventoryDate;

    private Integer availableQuantity;
}