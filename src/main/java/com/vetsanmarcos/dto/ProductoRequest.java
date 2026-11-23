package com.vetsanmarcos.dto;

import jakarta.validation.constraints.*;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio.") @Size(max = 100, message = "El nombre admite máximo 100 caracteres.") String nombre,
        @NotBlank(message = "Selecciona una categoría.") @Pattern(regexp = "\\d+", message = "La categoría no es válida.") String categoriaId,
        @NotBlank(message = "La especie es obligatoria.") @Size(max = 60) String especie,
        @NotNull(message = "El precio es obligatorio.") @Min(value = 1, message = "El precio debe ser mayor a 0.") @Max(10_000_000) Integer precio,
        @NotNull(message = "El descuento es obligatorio.") @Min(value = 0, message = "El descuento mínimo es 0.") @Max(value = 90, message = "El descuento máximo es 90.") Integer descuento,
        @NotNull(message = "El stock es obligatorio.") @Min(value = 0, message = "El stock no puede ser negativo.") @Max(100_000) Integer stock,
        @NotNull(message = "El stock crítico es obligatorio.") @Min(0) @Max(100_000) Integer stockCritico,
        @NotBlank(message = "La descripción es obligatoria.") @Size(max = 500, message = "La descripción admite máximo 500 caracteres.") String descripcion,
        @Size(max = 255) String imagen) {}
