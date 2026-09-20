package com.buyio.order.service;

import com.buyio.order.domain.*;
import com.buyio.order.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic-order:OrderEvents}")
    private String orderTopic;

    public List<PurchaseOrder> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public PurchaseOrder createOrder(OrderRequest req) {
        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber("PO-" + System.currentTimeMillis())
                .supplierId(req.supplierId())
                .expectedDeliveryDate(req.expectedDeliveryDate())
                .status(OrderStatus.INGRESADO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (var itemReq : req.items()) {
            BigDecimal lineTotal = itemReq.unitPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                    .order(order)
                    .productId(itemReq.productId())
                    .quantity(itemReq.quantity())
                    .unitPrice(itemReq.unitPrice())
                    .subtotal(lineTotal)
                    .build();
            order.getItems().add(detail);
            total = total.add(lineTotal);
        }
        order.setTotalAmount(total);

        PurchaseOrder savedOrder = orderRepository.save(order);

        // Emitir evento de creación a Kafka
        publishEvent("ORDER_CREATED", savedOrder);

        return savedOrder;
    }

    @Transactional
    public PurchaseOrder changeStatus(UUID id, OrderStatus newStatus) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        if (order.getStatus() == OrderStatus.ANULADA || order.getStatus() == OrderStatus.RECIBIDA) {
            throw new IllegalStateException("No se puede cambiar el estado de una orden en estado final.");
        }
        
        order.setStatus(newStatus);
        PurchaseOrder updatedOrder = orderRepository.save(order);

        // Emitir evento de cambio de estado a Kafka
        publishEvent("ORDER_STATUS_UPDATED", updatedOrder);

        return updatedOrder;
    }

    private void publishEvent(String eventType, PurchaseOrder order) {
        try {
            OrderEvent event = new OrderEvent(
                    eventType,
                    order.getId(),
                    order.getOrderNumber(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    order.getSupplierId(),
                    LocalDateTime.now().toString()
            );

            kafkaTemplate.send(orderTopic, order.getId().toString(), event);
            log.info("Evento [{}] publicado exitosamente en el tópico [{}]: ID Orden {}", eventType, orderTopic, order.getId());
        } catch (Exception e) {
            log.error("Error al publicar evento [{}] en Kafka para la orden {}", eventType, order.getId(), e);
        }
    }
}