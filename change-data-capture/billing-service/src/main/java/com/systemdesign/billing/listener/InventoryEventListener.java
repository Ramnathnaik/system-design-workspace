package com.systemdesign.billing.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdesign.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-updated", groupId = "billing-service-group")
    public void handleInventoryUpdate(String message) {
        try {
            log.info("Received inventory update event: {}", message);
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode data = jsonNode.get("data");
            
            Long orderId = data.get("order_id").asLong();
            String status = data.get("status").asText();
            
            // Only create invoice if inventory was reserved successfully
            if ("RESERVED".equals(status)) {
                // In a real scenario, fetch order details to get amount and customer ID
                // For demo, using placeholder values
                billingService.createInvoice(orderId, "CUSTOMER_" + orderId, BigDecimal.valueOf(100.00));
            }
            
        } catch (Exception e) {
            log.error("Error processing inventory update event", e);
        }
    }
}
