package com.vetsanmarcos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntregaDto(
        @NotBlank(message = "La calle es obligatoria.") @Size(max = 200) String calle,
        @Size(max = 50) String departamento,
        @NotBlank(message = "La región es obligatoria.") @Size(max = 100) String region,
        @NotBlank(message = "La comuna es obligatoria.") @Size(max = 100) String comuna,
        @Size(max = 300, message = "Las indicaciones admiten máximo 300 caracteres.") String indicaciones) {}
