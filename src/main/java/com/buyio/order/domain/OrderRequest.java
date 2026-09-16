package com.buyio.order.domain;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderRequest(
    @NotNull(message = "El proveedor es obligatorio") UUID supplierId,
    @NotNull(message = "La fecha estimada es obligatoria") LocalDate expectedDeliveryDate,
    @NotEmpty(message = "Debe incluir al menos un producto") List<OrderItemRequest> items
) {}

public record OrderItemRequest(
    @NotNull UUID productId,
    @NotNull @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity,
    @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0") BigDecimal unitPrice
) {}