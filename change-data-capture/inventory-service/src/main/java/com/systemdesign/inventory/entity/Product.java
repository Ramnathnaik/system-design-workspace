package com.systemdesign.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private String productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private Integer availableStock;
}
