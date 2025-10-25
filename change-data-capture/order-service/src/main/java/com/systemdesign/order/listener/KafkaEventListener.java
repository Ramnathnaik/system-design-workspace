package com.systemdesign.order.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdesign.order.entity.Order;
import com.systemdesign.order.entity.OrderStatus;
import com.systemdesign.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-updated", groupId = "order-service-group")
    public void handleInventoryUpdate(String message) {
        try {
            log.info("Received inventory update: {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode data = jsonNode.get("data");
            
            Long orderId = data.get("order_id").asLong();
            String status = data.get("status").asText();
            
            orderRepository.findById(orderId).ifPresent(order -> {
                if ("RESERVED".equals(status)) {
                    order.setStatus(OrderStatus.INVENTORY_RESERVED);
                    log.info("Order {} - Inventory reserved successfully", orderId);
                } else if ("FAILED".equals(status)) {
                    order.setStatus(OrderStatus.INVENTORY_FAILED);
                    log.info("Order {} - Inventory reservation failed", orderId);
                }
                orderRepository.save(order);
            });
        } catch (Exception e) {
            log.error("Error processing inventory update", e);
        }
    }

    @KafkaListener(topics = "billing-updated", groupId = "order-service-group")
    public void handleBillingUpdate(String message) {
        try {
            log.info("Received billing update: {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode data = jsonNode.get("data");
            
            Long orderId = data.get("order_id").asLong();
            String status = data.get("status").asText();
            
            orderRepository.findById(orderId).ifPresent(order -> {
                if ("INVOICED".equals(status)) {
                    order.setStatus(OrderStatus.BILLED);
                    log.info("Order {} - Invoice generated", orderId);
                } else if ("PAID".equals(status)) {
                    order.setStatus(OrderStatus.PAID);
                    log.info("Order {} - Payment received", orderId);
                }
                orderRepository.save(order);
            });
        } catch (Exception e) {
            log.error("Error processing billing update", e);
        }
    }
}
