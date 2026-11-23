package com.vetsanmarcos.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Edición del perfil propio: NO incluye rol ni contraseña. */
public record PerfilRequest(
        @NotBlank(message = "El nombre es obligatorio.") @Size(max = 50) String nombre,
        @NotBlank(message = "El apellido es obligatorio.") @Size(max = 100) String apellido,
        @NotNull(message = "La fecha de nacimiento es obligatoria.") @Past LocalDate fechaNacimiento,
        @NotBlank(message = "El género es obligatorio.") @Size(max = 30) String genero,
        @NotBlank(message = "El correo es obligatorio.") @Email(message = "El formato del correo no es válido.") @Size(max = 100) String correo,
        @NotBlank(message = "La dirección es obligatoria.") @Size(max = 300) String direccion,
        @NotBlank(message = "La región es obligatoria.") @Size(max = 100) String region,
        @NotBlank(message = "La comuna es obligatoria.") @Size(max = 100) String comuna) {}
