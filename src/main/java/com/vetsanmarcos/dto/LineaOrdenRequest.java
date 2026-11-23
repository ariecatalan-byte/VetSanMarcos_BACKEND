package com.vetsanmarcos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Solo id y cantidad: el PRECIO lo calcula el servidor (nunca se confía en el precio del cliente). */
public record LineaOrdenRequest(
        @NotBlank @Pattern(regexp = "\\d+", message = "El producto no es válido.") String idProducto,
        @NotNull @Min(value = 1, message = "La cantidad mínima es 1.") Integer cantidad) {}
