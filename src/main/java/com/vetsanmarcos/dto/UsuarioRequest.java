package com.vetsanmarcos.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Alta/edición de usuario (registro público, panel admin). La contraseña es opcional al editar. */
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio.") @Size(max = 50, message = "El nombre admite máximo 50 caracteres.") String nombre,
        @NotBlank(message = "El apellido es obligatorio.") @Size(max = 100, message = "El apellido admite máximo 100 caracteres.") String apellido,
        @NotNull(message = "La fecha de nacimiento es obligatoria.") @Past(message = "La fecha de nacimiento debe ser pasada.") LocalDate fechaNacimiento,
        @NotBlank(message = "El género es obligatorio.") @Size(max = 30) String genero,
        @NotBlank(message = "El correo es obligatorio.") @Email(message = "El formato del correo no es válido.") @Size(max = 100) String correo,
        String password,
        @NotBlank(message = "La dirección es obligatoria.") @Size(max = 300) String direccion,
        @NotBlank(message = "La región es obligatoria.") @Size(max = 100) String region,
        @NotBlank(message = "La comuna es obligatoria.") @Size(max = 100) String comuna,
        String rol) {}
