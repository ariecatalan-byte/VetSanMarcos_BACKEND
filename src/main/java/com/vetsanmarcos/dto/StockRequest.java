package com.vetsanmarcos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRequest(
        @NotNull(message = "El stock es obligatorio.") @Min(value = 0, message = "El stock no puede ser negativo.") @Max(100_000) Integer stock) {}
