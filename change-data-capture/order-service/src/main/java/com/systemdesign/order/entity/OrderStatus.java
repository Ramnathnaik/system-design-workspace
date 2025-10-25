package com.systemdesign.order.entity;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    BILLED,
    PAID,
    COMPLETED,
    CANCELLED
}
