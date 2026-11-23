package com.vetsanmarcos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteDto(
        @NotBlank(message = "El nombre del cliente es obligatorio.") @Size(max = 50) String nombre,
        @NotBlank(message = "Los apellidos son obligatorios.") @Size(max = 100) String apellidos,
        @NotBlank(message = "El correo es obligatorio.") @Email(message = "El correo no es válido.") @Size(max = 100) String correo) {}
