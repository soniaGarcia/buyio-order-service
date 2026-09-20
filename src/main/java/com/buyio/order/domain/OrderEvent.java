package com.buyio.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEvent(
    String eventType,
    UUID orderId,
    String orderNumber,
    String status,
    BigDecimal totalAmount,
    UUID supplierId,
    String timestamp
) {
    public static OrderEvent of(String eventType, PurchaseOrder order) {
        return new OrderEvent(
            eventType,
            order.getId(),
            order.getOrderNumber(),
            order.getStatus().name(),
            order.getTotalAmount(),
            order.getSupplierId(),
            Instant.now().toString() // "2026-09-20T01:07:18.815Z"
        );
    }
}