package com.buyio.order.dto;

import com.buyio.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDto {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}