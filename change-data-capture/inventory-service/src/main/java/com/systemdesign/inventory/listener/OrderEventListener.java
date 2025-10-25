package com.systemdesign.inventory.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdesign.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    public void handleOrderCreated(String message) {
        try {
            log.info("Received order created event: {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode data = jsonNode.get("data");
            
            Long orderId = data.get("id").asLong();
            String productId = data.get("product_id").asText();
            Integer quantity = data.get("quantity").asInt();
            
            // Reserve inventory for the order
            inventoryService.reserveInventory(orderId, productId, quantity);
            
        } catch (Exception e) {
            log.error("Error processing order created event", e);
        }
    }
}
