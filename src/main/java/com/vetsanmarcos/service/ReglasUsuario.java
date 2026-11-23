package com.vetsanmarcos.service;

import com.vetsanmarcos.exception.ApiException;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/** Reglas de negocio heredadas de EP1/EP2, ahora también validadas en el servidor. */
final class ReglasUsuario {

    static final int EDAD_MINIMA = 14;
    static final int MAX_INTENTOS = 3;
    private static final String DOMINIO = "@duoc.cl";
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,100}$");

    private ReglasUsuario() {}

    static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }

    static void validarCorreo(String correo) {
        if (!normalizarCorreo(correo).endsWith(DOMINIO)) {
            throw ApiException.invalida("correo", "Solo se aceptan correos institucionales " + DOMINIO);
        }
    }

    static void validarEdad(LocalDate fechaNacimiento) {
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < EDAD_MINIMA) {
            throw ApiException.invalida("fechaNacimiento", "Debes tener al menos " + EDAD_MINIMA + " años.");
        }
    }

    static void validarPassword(String password) {
        if (password == null || !PASSWORD.matcher(password).matches()) {
            throw ApiException.invalida("password", "La contraseña debe tener mínimo 8 caracteres, con al menos 1 letra y 1 número.");
        }
    }
}
