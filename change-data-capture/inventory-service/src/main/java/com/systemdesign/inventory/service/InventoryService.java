package com.systemdesign.inventory.service;

import com.systemdesign.inventory.entity.Inventory;
import com.systemdesign.inventory.entity.InventoryStatus;
import com.systemdesign.inventory.entity.Product;
import com.systemdesign.inventory.repository.InventoryRepository;
import com.systemdesign.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Inventory reserveInventory(Long orderId, String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = new Inventory();
        inventory.setOrderId(orderId);
        inventory.setProductId(productId);
        inventory.setQuantityReserved(quantity);
        inventory.setAvailableQuantity(product.getAvailableStock());

        if (product.getAvailableStock() >= quantity) {
            product.setAvailableStock(product.getAvailableStock() - quantity);
            productRepository.save(product);
            
            inventory.setStatus(InventoryStatus.RESERVED);
            log.info("Inventory reserved for Order {} - Product: {}, Quantity: {}", 
                    orderId, productId, quantity);
        } else {
            inventory.setStatus(InventoryStatus.FAILED);
            log.warn("Insufficient inventory for Order {} - Product: {}, Requested: {}, Available: {}", 
                    orderId, productId, quantity, product.getAvailableStock());
        }

        return inventoryRepository.save(inventory);
    }

    public Inventory getInventoryByOrderId(Long orderId) {
        return inventoryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for order"));
    }
}
