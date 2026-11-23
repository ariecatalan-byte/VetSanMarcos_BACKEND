package com.vetsanmarcos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** pagoAprobado: el pago es una SIMULACIÓN académica (no hay pasarela real). */
public record OrdenRequest(
        @NotEmpty(message = "El carrito está vacío.") @Valid List<LineaOrdenRequest> lineas,
        @NotNull(message = "Faltan los datos del cliente.") @Valid ClienteDto cliente,
        @NotNull(message = "Falta la dirección de entrega.") @Valid EntregaDto entrega,
        boolean pagoAprobado) {}
