package com.buyio.order.service;

import com.buyio.order.domain.Order;
import com.buyio.order.domain.OrderItem;
import com.buyio.order.domain.OrderStatus;
import com.buyio.order.dto.*;
import com.buyio.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequest request) {
        String generatedOrderNumber = "ORD-" + System.currentTimeMillis();

        Order order = Order.builder()
                .orderNumber(generatedOrderNumber)
                .userId(request.getUserId())
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto itemDto : request.getItems()) {
            BigDecimal subtotal = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .productId(itemDto.getProductId())
                    .productName(itemDto.getProductName())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(OrderEvent.builder()
                .eventType("ORDER_CREATED")
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .userId(savedOrder.getUserId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus())
                .timestamp(OffsetDateTime.now())
                .build());

        return mapToDto(savedOrder);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("La orden ya se encuentra cancelada");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(OrderEvent.builder()
                .eventType("ORDER_CANCELLED")
                .orderId(updatedOrder.getId())
                .orderNumber(updatedOrder.getOrderNumber())
                .userId(updatedOrder.getUserId())
                .totalAmount(updatedOrder.getTotalAmount())
                .status(updatedOrder.getStatus())
                .timestamp(OffsetDateTime.now())
                .build());

        return mapToDto(updatedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        return mapToDto(order);
    }

    private OrderResponseDto mapToDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(i -> OrderItemDto.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .subtotal(i.getSubtotal())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}