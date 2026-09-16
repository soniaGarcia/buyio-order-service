package com.buyio.order.service;

import com.buyio.order.domain.*;
import com.buyio.order.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;

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
        return orderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder changeStatus(UUID id, OrderStatus newStatus) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        if (order.getStatus() == OrderStatus.ANULADA || order.getStatus() == OrderStatus.RECIBIDA) {
            throw new IllegalStateException("No se puede cambiar el estado de una orden en estado final.");
        }
        
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}