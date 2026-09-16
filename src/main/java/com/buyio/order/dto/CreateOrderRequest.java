package com.buyio.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID userId;

    @NotEmpty(message = "La orden debe contener al menos un producto")
    @Valid
    private List<OrderItemDto> items;
}