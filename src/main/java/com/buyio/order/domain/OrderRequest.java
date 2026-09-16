package com.buyio.order.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderRequest(
    @NotNull(message = "El proveedor es obligatorio") UUID supplierId,
    @NotNull(message = "La fecha estimada es obligatoria") LocalDate expectedDeliveryDate,
    @NotEmpty(message = "Debe incluir al menos un producto") List<OrderItemRequest> items
) {}