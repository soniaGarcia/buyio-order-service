package com.buyio.order.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
    @NotNull UUID productId,
    @NotNull @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity,
    @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0") BigDecimal unitPrice
) {}