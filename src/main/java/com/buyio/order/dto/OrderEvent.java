package com.buyio.order.dto;

import com.buyio.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType; // ORDER_CREATED, ORDER_CANCELLED
    private UUID orderId;
    private String orderNumber;
    private UUID userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private OffsetDateTime timestamp;
}