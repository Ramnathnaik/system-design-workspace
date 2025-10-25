package com.systemdesign.billing.service;

import com.systemdesign.billing.entity.Invoice;
import com.systemdesign.billing.entity.InvoiceStatus;
import com.systemdesign.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    @Transactional
    public Invoice createInvoice(Long orderId, String customerId, BigDecimal amount) {
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderId);
        invoice.setCustomerId(customerId);
        invoice.setAmount(amount);
        invoice.setStatus(InvoiceStatus.INVOICED);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created for Order {} - Amount: {}", orderId, amount);
        return savedInvoice;
    }

    @Transactional
    public Invoice markAsPaid(Long orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for order"));
        
        invoice.setStatus(InvoiceStatus.PAID);
        log.info("Invoice marked as PAID for Order {}", orderId);
        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for order"));
    }
}
