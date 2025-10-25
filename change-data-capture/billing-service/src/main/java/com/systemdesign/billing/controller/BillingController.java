package com.systemdesign.billing.controller;

import com.systemdesign.billing.entity.Invoice;
import com.systemdesign.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(billingService.getInvoiceByOrderId(orderId));
    }

    @PostMapping("/invoice/{orderId}/pay")
    public ResponseEntity<Invoice> markInvoiceAsPaid(@PathVariable Long orderId) {
        return ResponseEntity.ok(billingService.markAsPaid(orderId));
    }
}
